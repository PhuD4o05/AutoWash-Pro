package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.scheduledTime BETWEEN :start AND :end AND b.status != 'CANCELLED'")
    long countActiveBookingsInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    Optional<Booking> findByQrCode(String qrCode);
    @Query("SELECT COUNT(b) FROM Booking b WHERE FUNCTION('DATE', b.scheduledTime) = :date")
    Long countByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(b) FROM Booking b WHERE FUNCTION('DATE', b.scheduledTime) BETWEEN :start AND :end")
    Long countByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    List<Booking> findByStatusAndCompletedTimeBetween(BookingStatus status, LocalDateTime start, LocalDateTime end);
    List<Booking> findByStatusInAndScheduledTimeBefore(List<BookingStatus> statuses, LocalDateTime time);
    List<Booking> findByCustomer(Customer customer);

}
