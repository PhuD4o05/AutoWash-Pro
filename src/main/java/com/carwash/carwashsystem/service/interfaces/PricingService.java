package com.carwash.carwashsystem.service.interfaces;

import java.time.LocalDateTime;

public interface PricingService {
    Double calculateFinalPrice(Long servicePackageId, LocalDateTime scheduledTime, String membershipTier, String voucherCode);
    // Có thể bỏ các method khác nếu chưa dùng
    default Long applyDynamicPricing(Long basePrice, LocalDateTime date, String membershipTier) { return basePrice; }
    default Long applyVoucher(Long price, String voucherCode) { return price; }
}