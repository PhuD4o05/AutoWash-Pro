package com.carwash.carwashsystem.service.interfaces;

public interface EmailService {
    void sendBookingConfirmation(String toEmail, String customerName, String bookingDetails, String qrCodeBase64);
    void sendQrCode(String toEmail, String qrCodeBase64, Long bookingId);
    void sendPasswordReset(String toEmail, String resetToken);

    void sendBookingConfirmation(String to, String qrCode, String bookingDetails);

        void sendQrCodeEmail(String toEmail, String customerName, byte[] qrCodeImage, String bookingId);
    }
