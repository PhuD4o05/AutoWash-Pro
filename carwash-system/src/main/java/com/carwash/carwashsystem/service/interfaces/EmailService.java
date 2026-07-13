package com.carwash.carwashsystem.service.interfaces;

public interface EmailService {

    // Email sau khi booking
    void sendBookingConfirmation(
            String toEmail,
            String customerName,
            String bookingDetails,
            String qrCodeBase64
    );


    // Email sau khi thanh toán thành công
    void sendPaymentSuccessEmail(
            String toEmail,
            String customerName,
            Long bookingId,
            Double amount
    );


    // ============================================================
    // 2. GỬI EMAIL QR CODE
    // ============================================================
    void sendQrCode(String toEmail, String qrCodeBase64, Long bookingId);

    void sendPasswordReset(
            String toEmail,
            String resetToken
    );


    // ============================================================
    // 4. GỬI EMAIL XÁC NHẬN (KHÔNG CÓ CUSTOMER NAME)
    // ============================================================
    void sendBookingConfirmation(String to, String qrCode, String bookingDetails);

    void sendQrCodeEmail(
            String toEmail,
            String customerName,
            byte[] qrCodeImage,
            String bookingId
    );

    void sendDepositSuccessEmail(String email, String fullName, Long id, double v);

    void sendBookingDepositEmail(
            String email,
            String customerName,
            Long bookingId,
            Double totalAmount,
            Double depositAmount,
            String depositQrBase64
    );
}