package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.PaymentRequest;
import com.carwash.carwashsystem.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(Long bookingId, PaymentRequest request);
    PaymentResponse confirmCashPayment(Long bookingId, Long receptionistId);
    PaymentResponse getPaymentByBooking(Long bookingId);
    PaymentResponse refundPayment(Long paymentId);

    // New methods for VietQR online payment
    String createOnlinePaymentLink(Long bookingId) throws Exception;
    void handlePaymentWebhook(String webhookBody) throws Exception;

}