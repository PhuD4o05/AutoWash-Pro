package com.carwash.carwashsystem.service.interfaces;

public interface QRCodeService {

    String generateQRCodeForBooking(String bookingToken);

    byte[] generateQRCodeImage(String text);
}