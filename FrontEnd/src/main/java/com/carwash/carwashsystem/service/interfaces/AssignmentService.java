package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.AssignBayRequest;
import com.carwash.carwashsystem.dto.response.WashAssignmentResponse;
import com.carwash.carwashsystem.entity.Assignment;

import java.util.List;

public interface AssignmentService {
    List<Assignment> getCurrentAssignments();

    WashAssignmentResponse assignWasherToBooking(Long bookingId, Long washerId);
    List<WashAssignmentResponse> getAssignmentsByWasher(Long washerId, String date);
    void autoAssignWashersForShift(String date, String shiftType);
}