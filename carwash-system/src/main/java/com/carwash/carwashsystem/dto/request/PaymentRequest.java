package com.carwash.carwashsystem.dto.request;

import com.carwash.carwashsystem.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull
    private Long amount;
    @NotNull
    private PaymentMethod paymentMethod;
    private PaymentMethod method;



}