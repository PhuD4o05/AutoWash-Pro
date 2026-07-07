package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.entity.ServicePackage;
import com.carwash.carwashsystem.repository.ServicePackageRepository;
import com.carwash.carwashsystem.service.interfaces.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {
    private final ServicePackageRepository packageRepository;

    @Override
    public Double calculateFinalPrice(Long servicePackageId, LocalDateTime scheduledTime, String membershipTier, String voucherCode) {
        ServicePackage sp = packageRepository.findById(servicePackageId)
                .orElseThrow(() -> new RuntimeException("Service package not found"));
        double basePrice = sp.getBasePrice(); // giả sử price là Double
        // TODO: logic dynamic pricing, membership, voucher
        return basePrice; // không trả về null
    }
}