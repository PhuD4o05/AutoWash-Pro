package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.PaymentRequest;
import com.carwash.carwashsystem.dto.response.PaymentResponse;
import com.carwash.carwashsystem.service.interfaces.AsyncService;      // ✅ Import
import com.carwash.carwashsystem.service.interfaces.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;                         // ✅ Import
import com.fasterxml.jackson.databind.ObjectMapper;                    // ✅ Import
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
    private final AsyncService asyncService;                    // Inject AsyncService
    private final ObjectMapper objectMapper = new ObjectMapper(); // Để parse JSON

    @PostMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RECEPTIONIST')")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable Long bookingId,
                                                          @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(bookingId, request));
    }

    //  Sửa webhook để gọi async
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String body) {
        try {
            // 1. Parse dữ liệu từ PayOS
            JsonNode json = objectMapper.readTree(body);
            Long bookingId = json.get("data").get("orderCode").asLong();
            String transactionId = json.get("data").get("transactionId").asText();

            log.info(" Nhận webhook từ PayOS: bookingId={}, transactionId={}", bookingId, transactionId);

            // 2. Gọi async xử lý (không đợi)
            asyncService.processPaymentSuccessAsync(bookingId, transactionId);

            // 3. Trả về OK ngay cho PayOS
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Lỗi webhook: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Invalid webhook");
        }
    }
    @GetMapping("/{bookingId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long bookingId){

        return ResponseEntity.ok(
                paymentService.getPaymentByBooking(bookingId)
        );
    }
}