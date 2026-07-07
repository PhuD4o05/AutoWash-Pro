package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.RedeemVoucherRequest;
import com.carwash.carwashsystem.dto.response.LoyaltyResponse;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.entity.LoyaltyTransaction;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.enums.LoyaltyTransactionType;
import com.carwash.carwashsystem.enums.MembershipTier;
import com.carwash.carwashsystem.exception.InsufficientPointsException;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.repository.LoyaltyTransactionRepository;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.service.interfaces.LoyaltyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoyaltyServiceImpl implements LoyaltyService {

    private final CustomerRepository customerRepository;
    private final LoyaltyTransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;

    @Override
    public LoyaltyResponse getCustomerLoyaltyInfo(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        Integer totalEarned = transactionRepository.sumEarnedPointsByCustomer(customerId);
        Integer totalRedeemed = transactionRepository.sumRedeemedPointsByCustomer(customerId);
        return LoyaltyResponse.builder()
                .currentPoints(customer.getCurrentPoints())
                .membershipTier(customer.getMembershipTier())
                .totalEarnedPoints(totalEarned != null ? totalEarned : 0)
                .totalRedeemedPoints(totalRedeemed != null ? totalRedeemed : 0)
                .build();
    }

    @Transactional
    @Override
    public void addPoints(Long customerId, int points, String referenceId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        customer.setTotalPoints(customer.getTotalPoints() + points);
        customer.setCurrentPoints(customer.getCurrentPoints() + points);
        updateMembershipTier(customer);
        customerRepository.save(customer);

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .customer(customer)
                .points(points)
                .type(LoyaltyTransactionType.EARNED)
                .description(referenceId) // dùng description thay cho referenceId
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void redeemPoints(Long customerId, int points, String voucherCode) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        if (customer.getCurrentPoints() < points) {
            throw new InsufficientPointsException("Not enough points");
        }
        customer.setCurrentPoints(customer.getCurrentPoints() - points);
        customerRepository.save(customer);

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .customer(customer)
                .points(points)
                .type(LoyaltyTransactionType.REDEEMED)
                .description("Redeemed voucher: " + voucherCode)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }

    private void updateMembershipTier(Customer customer) {
        int points = customer.getTotalPoints();
        if (points >= 3000) customer.setMembershipTier(MembershipTier.PLATINUM);
        else if (points >= 1500) customer.setMembershipTier(MembershipTier.GOLD);
        else if (points >= 500) customer.setMembershipTier(MembershipTier.SILVER);
        else customer.setMembershipTier(MembershipTier.MEMBER);
    }

    @Override
    @Transactional
    public void addPoints(Long customerId, Long bookingId, Long amountPaid) {
        // Tính điểm: 1 point = 10,000 VND (ví dụ)
        int points = (int)(amountPaid / 10000);
        if (points <= 0) return;
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        customer.setTotalPoints(customer.getTotalPoints() + points);
        customer.setCurrentPoints(customer.getCurrentPoints() + points);
        updateMembershipTier(customer);
        customerRepository.save(customer);

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
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
    public LoyaltyResponse getCustomerLoyalty(Long customerId) {
        return getCustomerLoyaltyInfo(customerId);
    }

    @Override
    public Page<LoyaltyResponse> getLoyaltyTransactions(Long customerId, Pageable pageable) {
        // TODO: mapping từ entity sang response
        return null;
    }

    @Override
    @Transactional
    public String redeemVoucher(RedeemVoucherRequest request, Long customerId) {
        redeemPoints(customerId, request.getPoints(), request.getVoucherCode());
        return "Voucher redeemed: " + request.getVoucherCode();
    }

    @Override
    @Transactional
    public void upgradeMembership(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        updateMembershipTier(customer);
        customerRepository.save(customer);
    }
}