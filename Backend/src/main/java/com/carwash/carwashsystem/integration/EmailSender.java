package com.carwash.carwashsystem.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import jakarta.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSender {
    private final JavaMailSender mailSender;

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("HTML email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendBookingConfirmation(String to, String customerName, String bookingDetails, String qrCodeBase64) {
        String subject = "Xác nhận đặt lịch rửa xe";
        String html = String.format("""
                <html><body>
                <h2>Xin chào %s,</h2>
                <p>Đặt lịch của bạn đã được xác nhận:</p>
                <p>%s</p>
                <p>Mã QR của bạn:</p>
                <img src="data:image/png;base64,%s" alt="QR Code"/>
                <p>Cảm ơn bạn đã sử dụng dịch vụ!</p>
                </body></html>
                """, customerName, bookingDetails, qrCodeBase64);
        sendHtmlEmail(to, subject, html);
    }
}