package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.WorkShift;
import com.carwash.carwashsystem.enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {
    List<WorkShift> findByWasherId(Long washerId);
    List<WorkShift> findByShiftDate(LocalDate date);
    List<WorkShift> findByShiftDateAndShiftType(LocalDate date, ShiftType shiftType);
    Optional<WorkShift> findByWasherIdAndShiftDateAndShiftType(Long washerId, LocalDate date, ShiftType shiftType);
    @Query("SELECT ws FROM WorkShift ws WHERE ws.shiftDate BETWEEN :startDate AND :endDate")
    List<WorkShift> findShiftsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}