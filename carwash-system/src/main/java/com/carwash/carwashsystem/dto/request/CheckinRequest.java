package com.carwash.carwashsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinRequest {
    @NotNull
    private Long vehicleId;
    @NotNull
    private Long packageId;
    @NotNull
    private Double totalPrice;// giá sau khi tính (có thể từ service)
    @NotBlank
    private String qrCode;
}