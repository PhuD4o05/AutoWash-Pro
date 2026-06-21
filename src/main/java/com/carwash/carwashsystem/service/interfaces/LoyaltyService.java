package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.RedeemVoucherRequest;
import com.carwash.carwashsystem.dto.response.LoyaltyResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoyaltyService {
    LoyaltyResponse getCustomerLoyaltyInfo(Long customerId);

    @Transactional
    void addPoints(Long customerId, int points, String referenceId);

    @Transactional
    void redeemPoints(Long customerId, int points, String voucherCode);

    void addPoints(Long customerId, Long bookingId, Long amountPaid);
    LoyaltyResponse getCustomerLoyalty(Long customerId);
    Page<LoyaltyResponse> getLoyaltyTransactions(Long customerId, Pageable pageable);
    String redeemVoucher(RedeemVoucherRequest request, Long customerId);
    void upgradeMembership(Long customerId);
}