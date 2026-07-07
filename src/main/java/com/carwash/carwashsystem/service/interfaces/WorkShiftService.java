package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.ShiftAssignmentRequest;
import com.carwash.carwashsystem.dto.request.WorkShiftRequest;
import com.carwash.carwashsystem.dto.response.WorkShiftResponse;
import com.carwash.carwashsystem.entity.WorkShift;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface WorkShiftService {
    List<WorkShift> getShiftsByWasher(Long washerId);

    List<WorkShift> getShiftsByDate(LocalDate date);

    @Transactional
    WorkShift assignShift(ShiftAssignmentRequest request);

    WorkShiftResponse createWorkShift(WorkShiftRequest request);
    WorkShiftResponse updateWorkShift(Long id, WorkShiftRequest request);
    void deleteWorkShift(Long id);
    Page<WorkShiftResponse> getShiftsByDate(LocalDate date, Pageable pageable);
    void autoGenerateShiftsForWeek(LocalDate startDate);
}