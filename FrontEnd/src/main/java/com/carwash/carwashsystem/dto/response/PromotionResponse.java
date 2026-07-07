package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.MembershipTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {
    private Long id;

    private String description;
    private Integer discountPercent;
    private Long discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;

    private MembershipTier applicableTier;
    private String name;
}