package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.UpdateWashStatusRequest;
import com.carwash.carwashsystem.dto.response.WashAssignmentResponse;
import com.carwash.carwashsystem.dto.response.WasherResponse;
import com.carwash.carwashsystem.entity.Washer;
import com.carwash.carwashsystem.repository.WasherRepository;
import com.carwash.carwashsystem.service.interfaces.WasherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WasherServiceImpl implements WasherService {

    private final WasherRepository washerRepository;

    @Override
    public List<WashAssignmentResponse> getAssignedVehicles(Long washerId, String date) {
        return List.of();
    }

    @Override
    public List<WashAssignmentResponse> getTodaySchedule(Long washerId) {
        return List.of();
    }

    @Override
    public WashAssignmentResponse updateWashStatus(UpdateWashStatusRequest request, Long washerId) {
        return null;
    }

    @Override
    public List<WasherResponse> getAllActiveWashers() {
        return washerRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Washer getWasherById(Long id) {
        return washerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Washer not found"));
    }

    private WasherResponse toResponse(Washer washer) {
        return WasherResponse.builder()
                .id(washer.getId())
                .fullName(washer.getFullName())
                .phoneNumber(washer.getPhoneNumber())
                .email(washer.getEmail())
                .isActive(washer.getIsActive())
                .build();
    }
}