package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.LoyaltyTransaction;
import com.carwash.carwashsystem.enums.LoyaltyTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface LoyaltyTransactionRepository
        extends JpaRepository<LoyaltyTransaction, Long> {


    Page<LoyaltyTransaction>
    findByCustomerId(Long customerId, Pageable pageable);



    @Query("""
            SELECT SUM(l.points)
            FROM LoyaltyTransaction l
            WHERE l.customer.id = :customerId
            AND l.type = com.carwash.carwashsystem.enums.LoyaltyTransactionType.EARNED
            """)
    Integer sumEarnedPointsByCustomer(
            @Param("customerId") Long customerId
    );



    @Query("""
            SELECT SUM(l.points)
            FROM LoyaltyTransaction l
            WHERE l.customer.id = :customerId
            AND l.type = com.carwash.carwashsystem.enums.LoyaltyTransactionType.REDEEMED
            """)
    Integer sumRedeemedPointsByCustomer(
            @Param("customerId") Long customerId
    );



    boolean existsByBookingIdAndType(
            Long bookingId,
            LoyaltyTransactionType type
    );

}