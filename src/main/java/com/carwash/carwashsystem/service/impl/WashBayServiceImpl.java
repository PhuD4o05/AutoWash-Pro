package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.AssignBayRequest;
import com.carwash.carwashsystem.dto.response.WashBayResponse;
import com.carwash.carwashsystem.entity.Assignment;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.WashBay;
import com.carwash.carwashsystem.entity.Washer;
import com.carwash.carwashsystem.enums.WashBayStatus;
import com.carwash.carwashsystem.repository.AssignmentRepository;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.WashBayRepository;
import com.carwash.carwashsystem.repository.WasherRepository;
import com.carwash.carwashsystem.service.interfaces.WashBayService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WashBayServiceImpl implements WashBayService {

    private final WashBayRepository washBayRepository;
    private final AssignmentRepository assignmentRepository;
    private final WasherRepository washerRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<WashBay> getAllBays() {
        return List.of();
    }

    @Override
    public List<WashBayResponse> getAllWashBays() {
        return washBayRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WashBayResponse getWashBayById(Long id) {
        return null;
    }

    @Override
    public WashBayResponse updateWashBayStatus(Long id, String status) {
        return null;
    }

    @Override
    @Transactional
    public WashBayResponse assignBayToBooking(AssignBayRequest request) {
        WashBay bay = washBayRepository.findById(request.getBayId())
                .orElseThrow(() -> new RuntimeException("Bay not found"));
        if (bay.getStatus() != WashBayStatus.AVAILABLE) {
            throw new RuntimeException("Bay not available");
        }
        bay.setStatus(WashBayStatus.OCCUPIED);
        washBayRepository.save(bay);

        Washer washer = washerRepository.findById(request.getWasherId())
                .orElseThrow(() -> new RuntimeException("Washer not found"));

        // Tạo assignment mới
        Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        Assignment assignment = Assignment.builder()
            .booking(booking)
            .washBay(bay)
            .washer(washer)
            .startTime(LocalDateTime.now())
            .status("ACTIVE")
            .build();
        assignmentRepository.save(assignment);

        return toResponse(bay);
    }

    @Override
    @Transactional
    public void releaseBay(Long bayId) {
        WashBay bay = washBayRepository.findById(bayId)
                .orElseThrow(() -> new RuntimeException("Bay not found"));
        bay.setStatus(WashBayStatus.AVAILABLE);
        washBayRepository.save(bay);

        // Tìm assignment đang active của bay này và cập nhật
        assignmentRepository.findByWashBayIdAndStatus(bayId, "ACTIVE")
                .ifPresent(assignment -> {
                    assignment.setEndTime(LocalDateTime.now());
                    assignment.setStatus("COMPLETED");
                    assignmentRepository.save(assignment);
                });
    }

    private WashBayResponse toResponse(WashBay bay) {
        return WashBayResponse.builder()
                .id(bay.getId())
                .bayNumber(bay.getBayNumber())
                .status(bay.getStatus())
                .build();
    }
}