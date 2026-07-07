package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.response.WashAssignmentResponse;
import com.carwash.carwashsystem.entity.Assignment;
import com.carwash.carwashsystem.repository.AssignmentRepository;
import com.carwash.carwashsystem.service.interfaces.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;

    @Override
    public List<Assignment> getCurrentAssignments() {
        return assignmentRepository.findCurrentAssignments(LocalDateTime.now());
    }

    @Override
    public WashAssignmentResponse assignWasherToBooking(Long bookingId, Long washerId) {
        return null;
    }

    @Override
    public List<WashAssignmentResponse> getAssignmentsByWasher(Long washerId, String date) {
        return List.of();
    }

    @Override
    public void autoAssignWashersForShift(String date, String shiftType) {

    }
}