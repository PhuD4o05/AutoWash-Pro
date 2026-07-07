//package com.carwash.carwashsystem.integration;
//
//import com.carwash.carwashsystem.exception.PaymentException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import vn.payos.PayOS;
//import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
//import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class PayosClient {
//    private final PayOS payOS;
//
//    @Value("${payos.return-url}")
//    private String returnUrl;
//
//    @Value("${payos.cancel-url}")
//    private String cancelUrl;
//
//    public String createPaymentLink(Long bookingId, Long amount, String description) {
//        try {
//            CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
//                    .orderCode(bookingId)
//                    .amount(amount)
//                    .description(description)
//                    .returnUrl(this.returnUrl)
//                    .cancelUrl(this.cancelUrl)
//                    .build();
//
//            CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentRequest);
//            String checkoutUrl = response.getCheckoutUrl();
//            log.info("Payment link created: {}", checkoutUrl);
//            return checkoutUrl;   // ✅ trả về String URL
//        } catch (Exception e) {
//            log.error("Failed to create PayOS payment link: {}", e.getMessage());
//            throw new PaymentException("Failed to initiate payment: " + e.getMessage());
//        }
//    }
//
//    // Tạm thời comment method verifyWebhookData vì SDK có thể thiếu class Webhook, WebhookData
//    /*
//    public WebhookData verifyWebhookData(Webhook webhookBody) {
//        ...
//    }
//    */
//}