package com.carwash.carwashsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePackageResponse {
    private Long id;
    private String name;
    private String description;
    private Integer estimatedMinutes;
    private Long basePrice;
    private String vehicleType;
    private Boolean isActive;
}
