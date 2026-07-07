package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.UpdateWashStatusRequest;
import com.carwash.carwashsystem.dto.response.WashAssignmentResponse;
import com.carwash.carwashsystem.dto.response.WasherResponse;
import com.carwash.carwashsystem.entity.Washer;

import java.util.List;

public interface WasherService {
    List<WashAssignmentResponse> getAssignedVehicles(Long washerId, String date);
    List<WashAssignmentResponse> getTodaySchedule(Long washerId);
    WashAssignmentResponse updateWashStatus(UpdateWashStatusRequest request, Long washerId);
    List<WasherResponse> getAllActiveWashers();

    Washer getWasherById(Long id);
}