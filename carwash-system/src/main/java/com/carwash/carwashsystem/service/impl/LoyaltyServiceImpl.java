package com.carwash.carwashsystem.service.impl;


import com.carwash.carwashsystem.dto.request.RedeemVoucherRequest;
import com.carwash.carwashsystem.dto.response.LoyaltyResponse;
import com.carwash.carwashsystem.dto.response.LoyaltyTransactionResponse;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.entity.LoyaltyTransaction;
import com.carwash.carwashsystem.enums.LoyaltyTransactionType;
import com.carwash.carwashsystem.enums.MembershipTier;
import com.carwash.carwashsystem.exception.InsufficientPointsException;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.repository.LoyaltyTransactionRepository;
import com.carwash.carwashsystem.service.interfaces.LoyaltyService;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class LoyaltyServiceImpl
        implements LoyaltyService {



    private final CustomerRepository customerRepository;

    private final LoyaltyTransactionRepository transactionRepository;

    private final BookingRepository bookingRepository;




    @Override
    public LoyaltyResponse getCustomerLoyaltyInfo(Long customerId){


        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow();



        Integer earned =
                transactionRepository.sumEarnedPointsByCustomer(customerId);



        Integer redeemed =
                transactionRepository.sumRedeemedPointsByCustomer(customerId);



        return LoyaltyResponse.builder()
                .currentPoints(
                        customer.getCurrentPoints()
                )
                .membershipTier(
                        customer.getMembershipTier()
                )
                .totalEarnedPoints(
                        earned == null ? 0 : earned
                )
                .totalRedeemedPoints(
                        redeemed == null ? 0 : redeemed
                )
                .build();

    }





    @Override
    @Transactional
    public void addPoints(Long customerId, Long bookingId, Long amountPaid) {


        // tránh cộng điểm nhiều lần cho cùng một booking
        if (transactionRepository.existsByBookingIdAndType(
                bookingId,
                LoyaltyTransactionType.EARNED
        )) {
            return;
        }


        // 1 point = 10.000 VNĐ
        int points = (int)(amountPaid / 10000);


        if (points <= 0) {
            return;
        }


        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found")
                );


        customer.setTotalPoints(
                customer.getTotalPoints() + points
        );


        customer.setCurrentPoints(
                customer.getCurrentPoints() + points
        );


        updateMembershipTier(customer);


        customerRepository.save(customer);



        Booking booking = bookingRepository.findById(bookingId)
                .orElse(null);



        LoyaltyTransaction transaction =
                LoyaltyTransaction.builder()
                        .customer(customer)
                        .booking(booking)
                        .points(points)
                        .type(LoyaltyTransactionType.EARNED)
                        .description("From booking #" + bookingId)
                        .createdAt(LocalDateTime.now())
                        .build();


        transactionRepository.save(transaction);
    }

    @Override
    public void addPoints(Long customerId, int points, String referenceId) {

    }


    @Override
    @Transactional
    public void redeemPoints(
            Long customerId,
            int points,
            String voucherCode
    ){


        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow();



        if(customer.getCurrentPoints()<points)
        {
            throw new InsufficientPointsException(
                    "Not enough points"
            );
        }



        customer.setCurrentPoints(
                customer.getCurrentPoints()-points
        );


        customerRepository.save(customer);



        LoyaltyTransaction transaction =
                LoyaltyTransaction.builder()
                        .customer(customer)
                        .points(points)
                        .type(
                                LoyaltyTransactionType.REDEEMED
                        )
                        .description(
                                "Redeem voucher "+voucherCode
                        )
                        .build();



        transactionRepository.save(transaction);

    }

    @Override
    public LoyaltyResponse getCustomerLoyalty(Long customerId) {
        return null;
    }


    private void updateMembershipTier(
            Customer customer
    ){


        int points =
                customer.getTotalPoints();



        if(points >=3000)
            customer.setMembershipTier(
                    MembershipTier.PLATINUM
            );


        else if(points>=1500)
            customer.setMembershipTier(
                    MembershipTier.GOLD
            );


        else if(points>=500)
            customer.setMembershipTier(
                    MembershipTier.SILVER
            );


        else
            customer.setMembershipTier(
                    MembershipTier.MEMBER
            );

    }







    @Override
    public void upgradeMembership(Long customerId){

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow();


        updateMembershipTier(customer);


        customerRepository.save(customer);

    }





    @Override
    public Page<LoyaltyTransactionResponse> getLoyaltyTransactions(
            Long customerId,
            Pageable pageable
    ){

        return transactionRepository
                .findByCustomerId(customerId,pageable)
                .map(t ->
                        LoyaltyTransactionResponse.builder()
                                .id(t.getId())
                                .points(t.getPoints())
                                .type(t.getType())
                                .description(t.getDescription())
                                .createdAt(t.getCreatedAt())
                                .build()
                );
    }



    @Override
    public String redeemVoucher(
            RedeemVoucherRequest request,
            Long customerId
    ){

        redeemPoints(
                customerId,
                request.getPoints(),
                request.getVoucherCode()
        );


        return "Redeem success";

    }

}