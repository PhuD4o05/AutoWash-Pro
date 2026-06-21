package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.Assignment;
import com.carwash.carwashsystem.entity.WashBay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByWasherId(Long washerId);
    List<Assignment> findByBookingId(Long bookingId);
    List<Assignment> findByWashBayId(Long washBayId);
    Optional<Assignment> findActiveAssignmentByBookingId(Long bookingId);

    // Sửa: so sánh đúng kiểu, dùng status = 'ACTIVE'
    @Query("SELECT a FROM Assignment a WHERE a.startTime <= :now AND (a.endTime IS NULL OR a.endTime > :now) AND a.status = 'ACTIVE'")
    List<Assignment> findCurrentAssignments(@Param("now") LocalDateTime now);

    List<Assignment> findByWasherIdAndStatus(Long washerId, String status);
    Optional<Assignment> findByWashBayIdAndStatus(Long washBayId, String status);
    Optional<Assignment> findByWashBayAndStatus(WashBay washBay, String status);

    // Giữ method không tham số nếu cần
    @Query("SELECT a FROM Assignment a WHERE a.endTime IS NULL AND a.status = 'ACTIVE'")
    List<Assignment> findCurrentAssignments();
}