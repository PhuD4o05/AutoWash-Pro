package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.WashQueue;
import com.carwash.carwashsystem.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WashQueueRepository extends JpaRepository<WashQueue, Long> {
    //List<WashQueue> findByStatusOrderByQueuedAtAsc(BookingStatus status);
    List<WashQueue> findByStatusInOrderByQueuePositionAsc(List<BookingStatus> statuses);
    Optional<WashQueue> findByBookingId(Long bookingId);
    @Modifying
    @Query("UPDATE WashQueue w SET w.status = :status WHERE w.id = :id")
    void updateQueueStatus(@Param("id") Long id, @Param("status") BookingStatus status);
    @Modifying
    @Query("DELETE FROM WashQueue w WHERE w.status IN ('COMPLETED', 'CANCELLED')")
    void removeCompletedAndCancelled();

    @Query("SELECT wq FROM WashQueue wq WHERE FUNCTION('DATE', wq.enqueuedAt) = CURRENT_DATE")
    List<WashQueue> findTodayActiveQueue();
    List<WashQueue> findByStatusOrderByEnqueuedAtAsc(BookingStatus status);
}
