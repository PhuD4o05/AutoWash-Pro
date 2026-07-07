package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.MembershipTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyResponse {
    private Integer currentPoints;
    private MembershipTier membershipTier;
    private Integer totalEarnedPoints;
    private Integer totalRedeemedPoints;
}