package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.PaymentMethod;
import com.carwash.carwashsystem.enums.PaymentStatus;
import com.carwash.carwashsystem.enums.PaymentType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private Long amount;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paidAt;
    private String checkoutUrl;// thêm dòng này
    private String qrCodeUrl;
    private PaymentType paymentType;

}