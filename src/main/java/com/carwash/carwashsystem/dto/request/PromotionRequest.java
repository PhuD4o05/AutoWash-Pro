package com.carwash.carwashsystem.dto.request;

import com.carwash.carwashsystem.enums.MembershipTier;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PromotionRequest {
    @NotBlank
    private String code;
    private String description;
    private String name;
    @NotNull
    private Integer discountPercent;
    private MembershipTier applicableTier; // null áp dụng cho tất cả
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    @Future
    private LocalDateTime endDate;
}