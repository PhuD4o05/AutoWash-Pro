package com.carwash.carwashsystem.dto.request;

import com.carwash.carwashsystem.enums.MembershipTier;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRuleRequest {
    private String ruleName;
    private Boolean isWeekend;
    private Boolean isHoliday;
    private LocalDate specificDate;
    private MembershipTier applicableTier; // null cho tất cả
    @NotNull
    private Integer percentAdjustment; // +10 (tăng 10%) hoặc -5
}