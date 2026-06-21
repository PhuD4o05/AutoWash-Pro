package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.LoyaltyTransaction;
import com.carwash.carwashsystem.enums.LoyaltyTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, Long> {
    List<LoyaltyTransaction> findByCustomerId(Long customerId);
    Page<LoyaltyTransaction> findByCustomerId(Long customerId, Pageable pageable);
    List<LoyaltyTransaction> findByCustomerIdAndType(Long customerId, LoyaltyTransactionType type);
    @Query("SELECT SUM(l.points) FROM LoyaltyTransaction l WHERE l.id = :customerId AND l.type = 'EARN'")
    Integer sumEarnedPointsByCustomer(@Param("customerId") Long customerId);
    @Query("SELECT SUM(l.points) FROM LoyaltyTransaction l WHERE l.id = :customerId AND l.type = 'REDEEM'")
    Integer sumRedeemedPointsByCustomer(@Param("customerId") Long customerId);
    @Query("SELECT l FROM LoyaltyTransaction l WHERE l.createdAt BETWEEN :start AND :end")
    List<LoyaltyTransaction> findByTransactionDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    boolean existsByBookingIdAndType(Long bookingId, LoyaltyTransactionType type);
}
