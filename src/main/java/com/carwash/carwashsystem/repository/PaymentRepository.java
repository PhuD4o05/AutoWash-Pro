package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.Payment;
import com.carwash.carwashsystem.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE DATE(p.paidAt) = :date")
    Long sumAmountByDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE DATE(p.paidAt) BETWEEN :start AND :end")
    Long sumAmountByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    Optional<Payment> findByBookingIdAndStatus(Long bookingId, PaymentStatus paymentStatus);


//    public interface paymentRepository extends JpaRepository<Payment, Long> {
//        Optional<Payment> findByBookingIdAndStatus(Long bookingId, PaymentStatus status);
//    }

}