package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.Payment;
import com.carwash.carwashsystem.enums.PaymentStatus;
import com.carwash.carwashsystem.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE DATE(p.paidAt) = :date")
    Long sumAmountByDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE DATE(p.paidAt) BETWEEN :start AND :end")
    Long sumAmountByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    Optional<Payment> findByBookingIdAndStatus(Long bookingId, PaymentStatus paymentStatus);
    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findFirstByBookingId(Long bookingId);

    // Deposit
    Optional<Payment> findByBookingIdAndPaymentType(
            Long bookingId,
            PaymentType paymentType
    );
    Optional<Payment> findByBookingIdAndPaymentTypeAndStatus(
            Long bookingId,
            PaymentType paymentType,
            PaymentStatus status
    );

    boolean existsByBookingIdAndPaymentTypeAndStatus(
            Long bookingId,
            PaymentType paymentType,
            PaymentStatus status
    );

    // Danh sách Payment
    List<Payment> findByBookingId(Long bookingId);

    // Tổng tiền đã thanh toán
    @Query("""
SELECT COALESCE(SUM(p.amount),0)
FROM Payment p
WHERE p.booking.id=:bookingId
AND p.status='PAID'
""")
    Long getTotalPaid(@Param("bookingId") Long bookingId);

    // Deposit
    @Query("""
SELECT p
FROM Payment p
WHERE p.booking.id=:bookingId
AND p.paymentType='DEPOSIT'
AND p.status='PAID'
""")
    Optional<Payment> getDepositPayment(
            @Param("bookingId") Long bookingId
    );

    // Final
    @Query("""
SELECT p
FROM Payment p
WHERE p.booking.id=:bookingId
AND p.paymentType='FINAL'
""")
    Optional<Payment> getFinalPayment(
            @Param("bookingId") Long bookingId
    );






}