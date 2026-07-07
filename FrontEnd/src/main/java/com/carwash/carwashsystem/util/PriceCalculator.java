package com.carwash.carwashsystem.util;

import com.carwash.carwashsystem.enums.MembershipTier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PriceCalculator {
    public static double applyWeekendSurcharge(double basePrice, LocalDateTime time) {
        if (time == null) return basePrice;
        LocalDate date = time.toLocalDate();
        boolean isWeekend = date.getDayOfWeek().getValue() >= 6; // Sat=6, Sun=7
        return isWeekend ? basePrice * 1.1 : basePrice;
    }

    public static double applyHolidaySurcharge(double basePrice, LocalDate date, boolean isHoliday) {
        return isHoliday ? basePrice * 1.2 : basePrice;
    }

    public static double applyMembershipDiscount(double price, MembershipTier tier) {
        double discount = LoyaltyCalculator.getDiscountPercent(tier);
        return price * (1 - discount / 100);
    }

    public static double applyVoucher(double price, double voucherDiscountPercent) {
        return price * (1 - voucherDiscountPercent / 100);
    }
}