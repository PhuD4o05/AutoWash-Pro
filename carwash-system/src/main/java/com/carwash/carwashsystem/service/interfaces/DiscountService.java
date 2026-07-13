package com.carwash.carwashsystem.service.interfaces;


public interface DiscountService {


    double calculateDiscount(
            double price,
            String voucherCode,
            String membershipTier
    );


}