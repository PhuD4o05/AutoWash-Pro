package com.carwash.carwashsystem.service.interfaces;

import java.time.LocalDateTime;

public interface PricingService {

    Long calculateFinalPrice(
            Long servicePackageId,
            LocalDateTime scheduledTime,
            String membershipTier,
            String voucherCode
    );

    default Long applyDynamicPricing(
            Long basePrice,
            LocalDateTime date,
            String membershipTier
    ){
        return basePrice;
    }

    default Long applyVoucher(
            Long price,
            String voucherCode
    ){
        return price;
    }
}