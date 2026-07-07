package com.carwash.carwashsystem.dto.request;

import jakarta.validation.constraints.Min;
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
public class ServicePackageRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Min(1)
    private Integer durationMinutes;
    @NotNull
    @Min(0)
    private Double price;
    private String vehicleType; // Car, SUV, v.v.
    // Trong ServicePackageRequest.java, đảm bảo có:


// getters & setters
}