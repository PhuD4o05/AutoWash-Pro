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
    public Double calculateFinalPrice(
            Long servicePackageId,
            LocalDateTime scheduledTime,
            String membershipTier,
            String voucherCode
    ) {


        ServicePackage sp =
                packageRepository.findById(servicePackageId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service package not found"
                                ));



        // Giá gốc

        double basePrice =
                sp.getBasePrice();



        // Tính tổng tiền giảm

        double discount =
                discountService.calculateDiscount(
                        basePrice,
                        voucherCode,
                        membershipTier
                );



        // Giá sau giảm

        double finalPrice =
                basePrice - discount;



        // tránh âm tiền

        if(finalPrice < 0){
            finalPrice = 0;
        }



        return finalPrice;
    }
}