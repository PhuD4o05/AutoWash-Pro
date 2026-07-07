package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;           // hoặc paymentId, nhưng thống nhất là id
    private Long bookingId;
    private Long amount;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paidAt;
}