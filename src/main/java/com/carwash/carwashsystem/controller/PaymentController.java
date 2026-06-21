package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.PaymentRequest;
import com.carwash.carwashsystem.dto.response.PaymentResponse;
import com.carwash.carwashsystem.service.interfaces.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Controller", description = "Xử lý thanh toán")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RECEPTIONIST')")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable Long bookingId,
                                                          @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(bookingId, request));
    }
    // Webhook endpoint cho PayOS thông báo kết quả thanh toán
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String body) {
        try {
            paymentService.handlePaymentWebhook(body);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Webhook error", e);
            return ResponseEntity.badRequest().body("Invalid webhook");
        }
    }

}
