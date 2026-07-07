package com.carwash.carwashsystem.util;

import com.carwash.carwashsystem.enums.MembershipTier;

public class LoyaltyCalculator {
    private static final int POINTS_PER_AMOUNT = 10000; // 1 point = 10,000 VND
    private static final int SILVER_THRESHOLD = 500;
    private static final int GOLD_THRESHOLD = 1500;
    private static final int PLATINUM_THRESHOLD = 3000;

    public static int calculatePoints(Long amountPaid) {
        if (amountPaid == null || amountPaid <= 0) return 0;
        return (int) (amountPaid / POINTS_PER_AMOUNT);
    }

    public static MembershipTier determineTier(int totalPoints) {
        if (totalPoints >= PLATINUM_THRESHOLD) return MembershipTier.PLATINUM;
        if (totalPoints >= GOLD_THRESHOLD) return MembershipTier.GOLD;
        if (totalPoints >= SILVER_THRESHOLD) return MembershipTier.SILVER;
        return MembershipTier.MEMBER;
    }

    public static double getDiscountPercent(MembershipTier tier) {
        switch (tier) {
            case SILVER: return 5.0;
            case GOLD: return 10.0;
            case PLATINUM: return 15.0;
            default: return 0.0;
        }
    }
}