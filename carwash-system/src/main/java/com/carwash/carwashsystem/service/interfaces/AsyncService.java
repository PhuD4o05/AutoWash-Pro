package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.Payment;

public interface AsyncService {

//    /**
//     * Xử lý thanh toán thành công (async)
//     * - Cập nhật payment status → PAID
//     * - Cập nhật booking status → CONFIRMED
//     * - Gửi email xác nhận
//     */
    void processPaymentSuccessAsync(Long bookingId, String transactionId);

//    /**
//     * Gửi email xác nhận đặt lịch (async)
//     */
    void sendBookingConfirmationAsync(Booking booking);

//    /**
//     * Gửi email xác nhận thanh toán (async)
//     */
    void sendPaymentConfirmationAsync(Payment payment);
}