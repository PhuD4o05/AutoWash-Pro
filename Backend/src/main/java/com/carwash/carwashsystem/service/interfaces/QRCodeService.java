package com.carwash.carwashsystem.service.interfaces;

public interface QRCodeService {
    String generateQRCodeForBooking(String data);
    boolean validateQRCode(String qrCode, Long bookingId);
    byte[] generateQrCode(String text, int width, int height);
}