package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.service.interfaces.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public void sendBookingConfirmation(String toEmail, String customerName, String bookingDetails, String qrCodeBase64) {

    }

    @Override
    public void sendQrCode(String toEmail, String qrCodeBase64, Long bookingId) {

    }

    @Override
    public void sendPasswordReset(String toEmail, String resetToken) {

    }

    @Override
    public void sendBookingConfirmation(String to, String qrCode, String bookingDetails) {

    }

    @Override
    public void sendQrCodeEmail(String toEmail, String customerName, byte[] qrCodeImage, String bookingId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đặt lịch rửa xe – Mã QR check-in");

            String checkinLink = baseUrl + "/api/checkin/qr?bookingId=" + bookingId;

            String htmlContent = "<!DOCTYPE html>" +
                    "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<h2>Chào " + customerName + ",</h2>" +
                    "<p>Cảm ơn bạn đã đặt lịch rửa xe tại <b>CarWash System</b>.</p>" +
                    "<p>Mã đặt lịch của bạn: <b>" + bookingId + "</b></p>" +
                    "<p>Vui lòng xuất trình mã QR dưới đây khi đến cửa hàng để check‑in nhanh chóng:</p>" +
                    "<img src='cid:qrCode' alt='QR Code' style='border: 1px solid #ccc; padding: 10px;'/><br/>" +
                    "<p>Hoặc truy cập link: <a href='" + checkinLink + "'>Check‑in ngay</a></p>" +
                    "<p>Trân trọng,<br/>Đội ngũ CarWash</p>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            helper.addInline("qrCode", new ByteArrayResource(qrCodeImage), "image/png");

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email QR: " + e.getMessage(), e);
        }
    }
}