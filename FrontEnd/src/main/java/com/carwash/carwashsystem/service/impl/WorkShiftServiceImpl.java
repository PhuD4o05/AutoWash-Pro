package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.ShiftAssignmentRequest;
import com.carwash.carwashsystem.dto.request.WorkShiftRequest;
import com.carwash.carwashsystem.dto.response.WorkShiftResponse;
import com.carwash.carwashsystem.entity.Washer;
import com.carwash.carwashsystem.entity.WorkShift;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.WasherRepository;
import com.carwash.carwashsystem.repository.WorkShiftRepository;
import com.carwash.carwashsystem.service.interfaces.WorkShiftService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkShiftServiceImpl implements WorkShiftService {

    private final WorkShiftRepository workShiftRepository;
    private final WasherRepository washerRepository;

    @Override
    public List<WorkShift> getShiftsByWasher(Long washerId) {
        return workShiftRepository.findByWasherId(washerId);
    }

    @Override
    public List<WorkShift> getShiftsByDate(LocalDate date) {
        return workShiftRepository.findByShiftDate(date);
    }

//    @Transactional
//    @Override
//    public WorkShift assignShift(ShiftAssignmentRequest request) {
//        WorkShift shift = WorkShift.builder()
//                .washer(washer)
//                .shiftDate(request.getShiftDate())
//                .shiftType(request.getShiftType())
//                .build();
//        return workShiftRepository.save(shift);
//    }
@Override
@Transactional
public WorkShift assignShift(ShiftAssignmentRequest request) {

    Washer washer = washerRepository.findById(request.getWasherId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("Washer not found"));

    WorkShift shift = WorkShift.builder()
            .washer(washer)
            .shiftDate(request.getShiftDate())
            .shiftType(request.getShiftType())
            .build();

    return workShiftRepository.save(shift);
}

    @Override
    public WorkShiftResponse createWorkShift(WorkShiftRequest request) {
        return null;
    }

    @Override
    public WorkShiftResponse updateWorkShift(Long id, WorkShiftRequest request) {
        return null;
    }

    @Override
    public void deleteWorkShift(Long id) {

    }

    @Override
    public Page<WorkShiftResponse> getShiftsByDate(LocalDate date, Pageable pageable) {
        return null;
    }

    @Override
    public void autoGenerateShiftsForWeek(LocalDate startDate) {

    }
}
