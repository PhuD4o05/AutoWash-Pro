package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.PaymentRequest;
import com.carwash.carwashsystem.dto.response.PaymentResponse;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.Payment;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.enums.PaymentMethod;
import com.carwash.carwashsystem.enums.PaymentStatus;
import com.carwash.carwashsystem.enums.PaymentType;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.integration.PayosClient;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.PaymentRepository;
import com.carwash.carwashsystem.service.interfaces.EmailService;
import com.carwash.carwashsystem.service.interfaces.LoyaltyService;
import com.carwash.carwashsystem.service.interfaces.PaymentService;
import com.carwash.carwashsystem.service.interfaces.QRCodeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PayosClient payosClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmailService emailService;
//    private final LoyaltyService loyaltyService;
    private final QRCodeService qrCodeService;

    @Override
    @Transactional
    public PaymentResponse processPayment(Long bookingId,
                                          PaymentRequest request) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found"));

        // Tổng tiền sau khi cộng dịch vụ phát sinh
        long finalAmount = booking.getTotalPrice().longValue();

        if (booking.getExtraServices() != null) {
            finalAmount += booking.getExtraServices()
                    .stream()
                    .mapToLong(s -> s.getPrice().longValue())
                    .sum();
        }

        long deposit = booking.getDepositAmount() == null
                ? 0
                : booking.getDepositAmount();

        long remain = finalAmount - deposit;

        if (remain < 0) {
            remain = 0;
        }

        //------------------------------------
        // KHÁCH CHỌN TIỀN MẶT
        //------------------------------------
        if (request.getMethod() == PaymentMethod.CASH) {

            Payment payment = Payment.builder()
                    .booking(booking)
                    .amount(remain)
                    .paymentType(PaymentType.FINAL)
                    .method(PaymentMethod.CASH)
                    .status(PaymentStatus.PAID)
                    .paidAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(payment);

            booking.setPaid(true);

            bookingRepository.save(booking);

            return PaymentResponse.builder()
                    .bookingId(bookingId)
                    .amount(remain)
                    .status(PaymentStatus.PAID)
                    .build();
        }

        //------------------------------------
        // KHÁCH CHỌN PAYOS
        //------------------------------------
        CreatePaymentLinkResponse payos =
                payosClient.createPaymentLink(
                        booking.getId(),
                        remain,
                        "Thanh toan con lai Booking #" + booking.getId()
                );

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(remain)
                .paymentType(PaymentType.FINAL)
                .method(PaymentMethod.PAYOS)
                .status(PaymentStatus.PENDING)
                .transactionId(String.valueOf(payos.getOrderCode()))
                .checkoutUrl(payos.getCheckoutUrl())
                .qrCodeUrl(payos.getQrCode())
                .build();

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .bookingId(bookingId)
                .amount(remain)
                .status(PaymentStatus.PENDING)
                .checkoutUrl(payos.getCheckoutUrl())
                .qrCodeUrl(payos.getQrCode())
                .build();
    }

    @Override
    public PaymentResponse confirmCashPayment(Long bookingId, Long receptionistId) {
        return null;
    }

    @Override
    @Transactional
    public void handlePaymentWebhook(String webhookBody) {

        try {

            // Verify webhook
            payosClient.verifyWebhook(webhookBody);

            JsonNode json = objectMapper.readTree(webhookBody);

            String status =
                    json.get("data").get("status").asText();

            Long bookingId =
                    json.get("data").get("orderCode").asLong();

            String transactionId =
                    json.get("data").get("transactionId").asText();

            if (!"PAID".equals(status)) {
                return;
            }

//            Payment payment =
//                    paymentRepository
//                            .findByTransactionId(String.valueOf(bookingId))
//                            .orElseThrow(() ->
//                                    new ResourceNotFoundException("Payment not found"));
            Payment payment =
                    paymentRepository
                            .findByTransactionId(
                                    String.valueOf(
                                            json.get("data")
                                                    .get("orderCode")
                                                    .asLong()
                                    )
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Payment not found"
                                    ));

            // webhook gọi nhiều lần
            if (payment.getStatus() == PaymentStatus.PAID) {
                return;
            }

            payment.setStatus(PaymentStatus.PAID);
            payment.setTransactionId(transactionId);
            payment.setPaidAt(LocalDateTime.now());

            paymentRepository.save(payment);

            Booking booking = payment.getBooking();

            booking.setDeposited(true);

            booking.setStatus(BookingStatus.CONFIRMED);

            bookingRepository.save(booking);

            log.info("Deposit success booking {}", bookingId);

            emailService.sendDepositSuccessEmail(
                    booking.getCustomer().getEmail(),
                    booking.getCustomer().getFullName(),
                    booking.getId(),
                    payment.getAmount().doubleValue()
            );

        } catch (Exception e) {

            log.error("Webhook processing error", e);

            throw new RuntimeException(
                    "Webhook processing failed",
                    e
            );
        }
    }

    @Override
    @Transactional
    public PaymentResponse createPaymentForBooking(Booking booking) {

        try {

            // Nếu đã tạo payment đặt cọc thì trả luôn
            Optional<Payment> oldPayment =
                    paymentRepository.findByBookingIdAndPaymentType(
                            booking.getId(),
                            PaymentType.DEPOSIT
                    );

            if (oldPayment.isPresent()) {

                Payment payment = oldPayment.get();

                return PaymentResponse.builder()
                        .id(payment.getId())
                        .bookingId(booking.getId())
                        .amount(payment.getAmount())
                        .status(payment.getStatus())
                        .transactionId(payment.getTransactionId())
                        .checkoutUrl(payment.getCheckoutUrl())
                        .qrCodeUrl(payment.getQrCodeUrl())
                        .build();
            }

            // Tính tiền cọc 30%
            Long depositAmount =
                    Math.round(booking.getTotalPrice() * 0.3);

            booking.setDepositAmount(depositAmount);
            booking.setDeposited(false);

            bookingRepository.save(booking);

            // Tạo link PayOS
            CreatePaymentLinkResponse payosResponse =
                    payosClient.createPaymentLink(
                            booking.getId(),
                            depositAmount,
                            "Dat coc booking #" + booking.getId()
                    );

            Payment payment =
                    Payment.builder()
                            .booking(booking)
                            .paymentType(PaymentType.DEPOSIT)
                            .amount(depositAmount)
                            .method(PaymentMethod.PAYOS)
                            .status(PaymentStatus.PENDING)
                            .transactionId(
                                    String.valueOf(
                                            payosResponse.getOrderCode()
                                    )
                            )
                            .checkoutUrl(
                                    payosResponse.getCheckoutUrl()
                            )
                            .qrCodeUrl(
                                    payosResponse.getQrCode()
                            )
                            .build();

            paymentRepository.save(payment);

            // Gửi email chứa QR check-in + QR thanh toán cọc
//            emailService.sendBookingDepositEmail(
//                    booking.getCustomer().getEmail(),
//                    booking.getCustomer().getFullName(),
//                    booking.getId(),
//                    booking.getTotalPrice().doubleValue(),
//                    depositAmount.doubleValue(),
//                    //payosResponse.getQrCode()
//                    payosResponse.getCheckoutUrl()
            byte[] checkinQr =
                    qrCodeService.generateQRCodeImage(
                            booking.getQrCode()
                    );

            byte[] payosQr =
                    qrCodeService.generateQRCodeImage(
                            payosResponse.getCheckoutUrl()
                    );

            emailService.sendBookingDepositEmail(
                    booking.getCustomer().getEmail(),
                    booking.getCustomer().getFullName(),
                    booking.getId(),
                    booking.getScheduledTime(),
                    booking.getServicePackage().getName(),
                    booking.getTotalPrice().doubleValue(),
                    depositAmount.doubleValue(),
                    checkinQr,
                    payosQr,
                    payosResponse.getCheckoutUrl()
            );


            return PaymentResponse.builder()
                    .id(payment.getId())
                    .bookingId(booking.getId())
                    .amount(payment.getAmount())
                    .status(payment.getStatus())
                    .transactionId(payment.getTransactionId())
                    .checkoutUrl(payment.getCheckoutUrl())
                    .qrCodeUrl(payment.getQrCodeUrl())
                    .build();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Không thể tạo thanh toán đặt cọc",
                    e
            );
        }
    }



    @Transactional
    @Override
    public PaymentResponse confirmCashPayment(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found"));

        Payment payment = paymentRepository
                .findFirstByBookingId(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        // Đã thanh toán đủ
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Booking đã thanh toán đầy đủ.");
        }

        // Tổng tiền cuối cùng (đã bao gồm dịch vụ phát sinh nếu có)
        long totalAmount = Math.round(booking.getTotalPrice());

        // Tiền đã đặt cọc
        long depositAmount = payment.getDepositAmount() == null
                ? 0L
                : payment.getDepositAmount();

        // Tiền còn lại phải thu
        long remainingAmount = totalAmount - depositAmount;

        if (remainingAmount < 0) {
            remainingAmount = 0;
        }

        // Thanh toán phần còn lại bằng tiền mặt
        payment.setMethod(PaymentMethod.CASH);
        payment.setAmount(totalAmount);
        payment.setRemainingAmount(0L);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        booking.setPaid(true);

        // Chỉ xác nhận đã thanh toán.
        // Booking sẽ COMPLETED khi xe rửa xong.
        booking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(booking);

        // Gửi email xác nhận thanh toán
        emailService.sendPaymentSuccessEmail(
                booking.getCustomer().getEmail(),
                booking.getCustomer().getFullName(),
                booking.getId(),
                (double) totalAmount
        );

        return mapPayment(payment);
    }

    @Override
    public PaymentResponse getPaymentByBooking(Long bookingId) {

        Payment payment =
                paymentRepository.findFirstByBookingId(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found"
                                ));

        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(bookingId)
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .paidAt(payment.getPaidAt())
                .build();
    }

    @Override
    public PaymentResponse refundPayment(Long paymentId) {
        // TODO: implement later
        throw new UnsupportedOperationException("Chưa implement");
    }

    @Override
    public String createOnlinePaymentLink(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        CreatePaymentLinkResponse response = payosClient.createPaymentLink(
                bookingId,
                booking.getTotalPrice().longValue(),
                "Thanh toan booking " + bookingId
        );
        return response.getCheckoutUrl();
    }
    private PaymentResponse mapPayment(Payment payment){

        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .paidAt(payment.getPaidAt())
                .checkoutUrl(payment.getCheckoutUrl())
                .qrCodeUrl(payment.getQrCodeUrl())
                .build();
    }

}