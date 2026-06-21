package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.MembershipTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicPriceRuleResponse {
    private Long id;
    private String ruleName;
    private Boolean isWeekend;
    private Boolean isHoliday;
    private LocalDate specificDate;
    private MembershipTier applicableTier;
    private Integer percentAdjustment;
    private Boolean isActive;
}
