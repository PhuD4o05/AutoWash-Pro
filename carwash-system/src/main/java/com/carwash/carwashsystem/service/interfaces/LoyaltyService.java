package com.carwash.carwashsystem.service.interfaces;


import com.carwash.carwashsystem.dto.request.RedeemVoucherRequest;
import com.carwash.carwashsystem.dto.response.LoyaltyResponse;
import com.carwash.carwashsystem.dto.response.LoyaltyTransactionResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LoyaltyService {


    LoyaltyResponse getCustomerLoyaltyInfo(Long customerId);



    void addPoints(
            Long customerId,
            Long bookingId,
            Long amountPaid
    );


    @Transactional
    void addPoints(Long customerId, int points, String referenceId);

    void redeemPoints(
            Long customerId,
            int points,
            String voucherCode
    );


    LoyaltyResponse getCustomerLoyalty(Long customerId);

    Page<LoyaltyTransactionResponse> getLoyaltyTransactions(
            Long customerId,
            Pageable pageable
    );



    String redeemVoucher(
            RedeemVoucherRequest request,
            Long customerId
    );



    void upgradeMembership(Long customerId);



}