package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.service.interfaces.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Override
    public String generateQRCodeForBooking(String bookingToken) {
        return bookingToken;
    }

    @Override
    public byte[] generateQRCodeImage(String text) {

        try {

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix =
                    qrCodeWriter.encode(
                            text,
                            BarcodeFormat.QR_CODE,
                            300,
                            300
                    );

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(
                    bitMatrix,
                    "PNG",
                    output
            );

            return output.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException("Cannot generate QR Code", e);

        }

    }
}