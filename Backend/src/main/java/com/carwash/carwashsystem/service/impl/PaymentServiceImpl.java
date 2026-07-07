package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.config.PayosApiProperties;
import com.carwash.carwashsystem.dto.request.PaymentRequest;
import com.carwash.carwashsystem.dto.response.PaymentResponse;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.Payment;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.enums.PaymentMethod;
import com.carwash.carwashsystem.enums.PaymentStatus;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.PaymentRepository;
import com.carwash.carwashsystem.service.interfaces.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PayosApiProperties payosProps;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public PaymentResponse processPayment(Long bookingId, PaymentRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (request.getPaymentMethod() == PaymentMethod.VIETQR) {
            try {
                String paymentUrl = createPayOSPaymentLink(booking);
                Payment payment = Payment.builder()
                        .booking(booking)
                        .amount(booking.getTotalPrice().longValue())   // ✅ ép sang Long
                        .method(PaymentMethod.VIETQR)
                        .status(PaymentStatus.PENDING)
                        .transactionId(paymentUrl)
                        .build();
                paymentRepository.save(payment);
                return PaymentResponse.builder()
                        .id(payment.getId())
                        .bookingId(bookingId)
                        .amount(payment.getAmount())
                        .status(PaymentStatus.PENDING)
                        .transactionId(paymentUrl)
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Lỗi tạo link thanh toán: " + e.getMessage());
            }
        }

        // Các phương thức khác
        Long amountValue = request.getAmount() != null ? request.getAmount().longValue() : booking.getTotalPrice().longValue();
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(amountValue)
                .method(request.getPaymentMethod())
                .status(PaymentStatus.PAID)
                .paidAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(bookingId)
                .amount(payment.getAmount())
                .status(PaymentStatus.PAID)
                .paidAt(payment.getPaidAt())
                .build();
    }

    private String createPayOSPaymentLink(Booking booking) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("orderCode", booking.getId());
        requestBody.put("amount", booking.getTotalPrice().intValue());
        requestBody.put("description", "Thanh toan booking " + booking.getId());
        requestBody.put("returnUrl", payosProps.getReturnUrl());
        requestBody.put("cancelUrl", payosProps.getCancelUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", payosProps.getClientId());
        headers.set("x-api-key", payosProps.getApiKey());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                payosProps.getCreatePaymentUrl(),
                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.get("data").get("checkoutUrl").asText();
        } else {
            throw new RuntimeException("PayOS error: " + response.getBody());
        }
    }

    @Override
    @Transactional
    public void handlePaymentWebhook(String webhookBody) throws Exception {
        JsonNode json = objectMapper.readTree(webhookBody);
        String status = json.get("status").asText();
        Long bookingId = json.get("orderCode").asLong();
        if ("PAID".equals(status)) {
            Payment payment = paymentRepository.findByBookingIdAndStatus(bookingId, PaymentStatus.PENDING)
                    .orElseGet(() -> {
                        Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
                        return Payment.builder()
                                .booking(booking)
                                .amount(booking.getTotalPrice().longValue())
                                .method(PaymentMethod.VIETQR)
                                .status(PaymentStatus.PAID)
                                .transactionId(json.has("transactionId") ? json.get("transactionId").asText() : null)
                                .paidAt(LocalDateTime.now())
                                .build();
                    });
            if (payment.getStatus() != PaymentStatus.PAID) {
                payment.setStatus(PaymentStatus.PAID);
                payment.setPaidAt(LocalDateTime.now());
                paymentRepository.save(payment);
                Booking booking = payment.getBooking();
                booking.setStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
                log.info("Booking {} confirmed after payment", bookingId);
            }
        }
    }

    @Override
    public PaymentResponse confirmCashPayment(Long bookingId, Long receptionistId) {
        throw new UnsupportedOperationException("Chưa implement");
    }

    @Override
    public PaymentResponse getPaymentByBooking(Long bookingId) {
        throw new UnsupportedOperationException("Chưa implement");
    }

    @Override
    public PaymentResponse refundPayment(Long paymentId) {
        throw new UnsupportedOperationException("Chưa implement");
    }

    @Override
    public String createOnlinePaymentLink(Long bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return createPayOSPaymentLink(booking);
    }
}