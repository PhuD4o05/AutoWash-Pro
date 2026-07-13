package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.Payment;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.enums.PaymentStatus;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.PaymentRepository;
import com.carwash.carwashsystem.service.interfaces.AsyncService;
import com.carwash.carwashsystem.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncServiceImpl implements AsyncService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    @Async
    @Transactional
    @Override
    public void processPaymentSuccessAsync(Long bookingId, String transactionId) {
        try {
            log.info(" Bắt đầu xử lý async thanh toán cho booking: {}", bookingId);

            // 1. Tìm và cập nhật Payment
            Payment payment = paymentRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with transactionId: " + transactionId));
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);
            log.info(" Đã cập nhật payment status = PAID");

            // 2. Tìm và cập nhật Booking
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            log.info(" Đã cập nhật booking status = CONFIRMED");

            // 3. Gửi email xác nhận (async)
            if (booking.getCustomer() != null && booking.getCustomer().getEmail() != null) {
                emailService.sendBookingConfirmation(
                        booking.getCustomer().getEmail(),
                        booking.getCustomer().getFullName(),
                        "Booking #" + bookingId + " - " + booking.getScheduledTime(),
                        null
                );
                log.info(" Đã gửi email xác nhận đến {}", booking.getCustomer().getEmail());
            }

            log.info(" Xử lý async thành công cho booking: {}", bookingId);
        } catch (Exception e) {
            log.error(" Lỗi khi xử lý async booking {}: {}", bookingId, e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendBookingConfirmationAsync(Booking booking) {
        try {
            if (booking.getCustomer() != null && booking.getCustomer().getEmail() != null) {
                emailService.sendBookingConfirmation(
                        booking.getCustomer().getEmail(),
                        booking.getCustomer().getFullName(),
                        "Booking #" + booking.getId() + " - " + booking.getScheduledTime(),
                        null
                );
                log.info(" Đã gửi email xác nhận cho booking: {}", booking.getId());
            }
        } catch (Exception e) {
            log.error(" Lỗi gửi email booking {}: {}", booking.getId(), e.getMessage());
        }
    }

    @Async
    @Override
    public void sendPaymentConfirmationAsync(Payment payment) {
        try {
            Booking booking = payment.getBooking();
            if (booking != null && booking.getCustomer() != null && booking.getCustomer().getEmail() != null) {
                emailService.sendQrCodeEmail(
                        booking.getCustomer().getEmail(),
                        booking.getCustomer().getFullName(),
                        null,
                        String.valueOf(booking.getId())
                );
                log.info("Đã gửi email xác nhận thanh toán cho booking: {}", booking.getId());
            }
        } catch (Exception e) {
            log.error(" Lỗi gửi email thanh toán: {}", e.getMessage());
        }
    }
}