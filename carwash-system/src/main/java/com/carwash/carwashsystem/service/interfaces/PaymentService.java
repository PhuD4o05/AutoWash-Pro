package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.PaymentRequest;
import com.carwash.carwashsystem.dto.response.PaymentResponse;
import com.carwash.carwashsystem.entity.Booking;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentService {

    PaymentResponse processPayment(Long bookingId, PaymentRequest request);

    PaymentResponse confirmCashPayment(Long bookingId, Long receptionistId);

    @Transactional
    PaymentResponse confirmCashPayment(Long bookingId);

    PaymentResponse getPaymentByBooking(Long bookingId);

    PaymentResponse refundPayment(Long paymentId);

    String createOnlinePaymentLink(Long bookingId);

    void handlePaymentWebhook(String webhookBody);

    PaymentResponse createPaymentForBooking(Booking booking);
}