package com.carwash.carwashsystem.integration;

import com.carwash.carwashsystem.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayosClient {
    private final PayOS payOS;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    public CreatePaymentLinkResponse createPaymentLink(Long bookingId, Long amount, String description) {
        try {
            CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
                    .orderCode(bookingId)
                    .amount(amount)
                    .description(description)
                    .returnUrl(this.returnUrl)
                    .cancelUrl(this.cancelUrl)
                    .build();

            CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentRequest);
            log.info("Payment link created: {}", response.getCheckoutUrl());
            return response;
        } catch (Exception e) {
            log.error("Failed to create PayOS payment link: {}", e.getMessage());
            throw new PaymentException("Failed to initiate payment: " + e.getMessage());
        }
    }

    public boolean verifyWebhook(String webhookBody) throws Exception {
        payOS.webhooks().verify(webhookBody);
        return true;
    }
}