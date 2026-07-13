package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.service.interfaces.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // ============================================================
    // 1. GỬI EMAIL XÁC NHẬN ĐẶT LỊCH (CÓ QR)
    // ============================================================
    @Override
    public void sendBookingConfirmation(String toEmail, String customerName, String bookingDetails, String qrCodeBase64) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đặt lịch rửa xe");

            String htmlContent =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "</head>" +
                            "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2>Chào " + customerName + ",</h2>" +
                    "<p>Cảm ơn bạn đã đặt lịch rửa xe tại <b>CarWash System</b>.</p>" +
                    "<p><b>Chi tiết booking:</b> " + bookingDetails + "</p>" +
                    (qrCodeBase64 != null ? "<img src='cid:qrCode' alt='QR Code'/><br/>" : "") +
                    "<p>Trân trọng,<br/>Đội ngũ CarWash</p>" +
                    "</body></html>";

            helper.setText(htmlContent, true);

            if (qrCodeBase64 != null) {
                byte[] qrBytes = Base64.getDecoder().decode(qrCodeBase64);
                helper.addInline("qrCode", new ByteArrayResource(qrBytes), "image/png");
            }

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email xác nhận: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendPaymentSuccessEmail(
            String toEmail,
            String customerName,
            Long bookingId,
            Double amount
    ) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");


            helper.setFrom(fromEmail);

            helper.setTo(toEmail);

            helper.setSubject(
                    "Thanh toán thành công - CarWash System"
            );


            String htmlContent =
                    "<!DOCTYPE html>"
                            + "<html>"
                            + "<body style='font-family: Arial;'>"

                            + "<h2>Xin chào "
                            + customerName
                            + "</h2>"

                            + "<p>"
                            + "Thanh toán booking của bạn đã thành công."
                            + "</p>"

                            + "<p>"
                            + "Mã booking: <b>"
                            + bookingId
                            + "</b>"
                            + "</p>"

                            + "<p>"
                            + "Số tiền: <b>"
                            + amount
                            + " VNĐ</b>"
                            + "</p>"

                            + "<p>"
                            + "Cảm ơn bạn đã sử dụng dịch vụ CarWash."
                            + "</p>"

                            + "<br>"

                            + "<p>"
                            + "Đội ngũ CarWash"
                            + "</p>"

                            + "</body>"
                            + "</html>";


            helper.setText(htmlContent, true);


            mailSender.send(message);


        } catch (Exception e) {

            throw new RuntimeException(
                    "Không thể gửi email thanh toán: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // ============================================================
    // 2. GỬI EMAIL QR CODE
    // ============================================================
    @Override
    public void sendQrCode(String toEmail, String qrCodeBase64, Long bookingId) {
        sendBookingConfirmation(toEmail, "Khách hàng", "Booking #" + bookingId, qrCodeBase64);
    }

    // ============================================================
    // 3. GỬI EMAIL ĐẶT LẠI MẬT KHẨU
    // ============================================================
    @Override
    public void sendPasswordReset(String toEmail, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(" Đặt lại mật khẩu - CarWash System");

            String resetLink = baseUrl + "/reset-password?token=" + resetToken;
            String htmlContent = "<!DOCTYPE html>" +
                    "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<h2>Đặt lại mật khẩu</h2>" +
                    "<p>Bạn vừa yêu cầu đặt lại mật khẩu.</p>" +
                    "<p>Nhấn vào link bên dưới để đặt lại mật khẩu:</p>" +
                    "<a href='" + resetLink + "'>" + resetLink + "</a>" +
                    "<p>Nếu không phải bạn, vui lòng bỏ qua email này.</p>" +
                    "<p>Trân trọng,<br/>Đội ngũ CarWash</p>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email reset password: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // 4. GỬI EMAIL XÁC NHẬN (KHÔNG CÓ CUSTOMER NAME)
    // ============================================================
    @Override
    public void sendBookingConfirmation(String to, String qrCode, String bookingDetails) {
        sendBookingConfirmation(to, "Khách hàng", bookingDetails, qrCode);
    }

    // ============================================================
    // 5. GỬI EMAIL QR CODE (DẠNG BYTE ARRAY) - ĐÃ CÓ
    // ============================================================
    @Override
    public void sendQrCodeEmail(String toEmail, String customerName, byte[] qrCodeImage, String bookingId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Mã QR check-in - CarWash System");

            String checkinLink = baseUrl + "/api/checkin/qr?bookingId=" + bookingId;

            String htmlContent = "<!DOCTYPE html>" +
                    "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<h2>Chào " + customerName + ",</h2>" +
                    "<p>Cảm ơn bạn đã đặt lịch rửa xe tại <b>CarWash System</b>.</p>" +
                    "<p>Mã đặt lịch của bạn: <b>" + bookingId + "</b></p>" +
                    "<p>Vui lòng xuất trình mã QR dưới đây khi đến cửa hàng để check‑in nhanh chóng:</p>" +
                    (qrCodeImage != null ? "<img src='cid:qrCode' alt='QR Code' style='border: 1px solid #ccc; padding: 10px;'/><br/>" : "") +
                    "<p>Hoặc truy cập link: <a href='" + checkinLink + "'>Check‑in ngay</a></p>" +
                    "<p>Trân trọng,<br/>Đội ngũ CarWash</p>" +
                    "</body></html>";

            helper.setText(htmlContent, true);

            if (qrCodeImage != null) {
                helper.addInline("qrCode", new ByteArrayResource(qrCodeImage), "image/png");
            }

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email QR: " + e.getMessage(), e);
        }
    }
    @Override
    public void sendDepositSuccessEmail(
            String toEmail,
            String customerName,
            Long bookingId,
            double amount
    ) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Đặt cọc thành công - CarWash System");

            String html =
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                    </head>
                    <body style="font-family:Arial,sans-serif">
    
                    <h2>Xin chào %s,</h2>
    
                    <p>Bạn đã <b>đặt cọc thành công</b> cho lịch rửa xe.</p>
    
                    <table border="1"
                           cellpadding="8"
                           cellspacing="0"
                           style="border-collapse:collapse">
    
                        <tr>
                            <td><b>Mã Booking</b></td>
                            <td>%d</td>
                        </tr>
    
                        <tr>
                            <td><b>Số tiền đặt cọc</b></td>
                            <td>%,.0f VNĐ</td>
                        </tr>
    
                        <tr>
                            <td><b>Trạng thái</b></td>
                            <td>Đã thanh toán tiền cọc</td>
                        </tr>
    
                    </table>
    
                    <br>
    
                    <p>
                    Khi đến cửa hàng, vui lòng xuất trình
                    <b>QR Check-in</b>.
                    </p>
    
                    <p>
                    Sau khi hoàn thành dịch vụ và nếu có phát sinh,
                    hệ thống sẽ tính tổng tiền cuối cùng.
                    Bạn chỉ cần thanh toán phần còn lại.
                    </p>
    
                    <br>
    
                    <p>
                    Cảm ơn bạn đã sử dụng dịch vụ của CarWash System.
                    </p>
    
                    </body>
                    </html>
                    """
                            .formatted(
                                    customerName,
                                    bookingId,
                                    amount
                            );

            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Không gửi được email đặt cọc",
                    e
            );
        }
    }

    @Override
    public void sendBookingDepositEmail(
            String email,
            String customerName,
            Long bookingId,
            Double totalAmount,
            Double depositAmount,
            //String depositQrBase64
            String paymentUrl
    ) {

        String subject = "Xác nhận đặt lịch rửa xe - Booking #" + bookingId;


        String content =
                "<h2>Xin chào " + customerName + "</h2>" +

                        "<p>Cảm ơn bạn đã đặt lịch tại CarWash System.</p>" +

                        "<p><b>Booking:</b> #" + bookingId + "</p>" +

                        "<p><b>Tổng tiền dịch vụ:</b> "
                        + totalAmount + " VNĐ</p>" +

                        "<p><b>Tiền cọc cần thanh toán:</b> "
                        + depositAmount + " VNĐ</p>" +

                        "<h3>Thanh toán tiền cọc</h3>" +

                        "<p>Vui lòng bấm vào link bên dưới để thanh toán:</p>" +

                        "<a href='"
                        + paymentUrl +
                        "' target='_blank'>"

                        + "Thanh toán PayOS"

                        + "</a>" +

                        "<p>Sau khi thanh toán cọc, vui lòng mang xe tới và quét QR check-in.</p>";


        sendEmail(
                email,
                subject,
                content
        );
    }
    private void sendEmail(
            String toEmail,
            String subject,
            String htmlContent
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Không thể gửi email: " + e.getMessage(),
                    e
            );
        }
    }

}