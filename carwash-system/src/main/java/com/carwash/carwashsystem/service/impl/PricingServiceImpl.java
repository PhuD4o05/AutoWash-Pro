package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.entity.ServicePackage;
import com.carwash.carwashsystem.repository.ServicePackageRepository;
import com.carwash.carwashsystem.service.interfaces.PricingService;
import com.carwash.carwashsystem.service.interfaces.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {


    private final ServicePackageRepository packageRepository;

    private final DiscountService discountService;



    @Override
    public Long calculateFinalPrice(
            Long servicePackageId,
            LocalDateTime scheduledTime,
            String membershipTier,
            String voucherCode
    ) {

        ServicePackage sp =
                packageRepository.findById(servicePackageId)
                        .orElseThrow(() ->
                                new RuntimeException("Service package not found"));

        Long basePrice = sp.getBasePrice();

        Long discount =
                Math.round(
                        discountService.calculateDiscount(
                                basePrice.doubleValue(),
                                voucherCode,
                                membershipTier
                        )
                );

        Long finalPrice = basePrice - discount;

        if (finalPrice < 0) {
            finalPrice = 0L;
        }

        return finalPrice;
    }
}