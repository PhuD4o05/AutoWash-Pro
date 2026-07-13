package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.response.WashAssignmentResponse;
import com.carwash.carwashsystem.entity.Assignment;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.Washer;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.AssignmentRepository;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.WasherRepository;
import com.carwash.carwashsystem.service.interfaces.AssignmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final BookingRepository bookingRepository;
    private final WasherRepository washerRepository;

    @Override
    public List<Assignment> getCurrentAssignments() {
        return assignmentRepository.findCurrentAssignments(LocalDateTime.now());
    }

    @Override
    @Transactional
    public WashAssignmentResponse assignWasherToBooking(Long bookingId, Long washerId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        Washer washer = washerRepository.findById(washerId)
                .orElseThrow(() -> new ResourceNotFoundException("Washer not found"));

        booking.setAssignedWasher(washer);

        bookingRepository.save(booking);

        Assignment assignment = Assignment.builder()
                .bookingId(bookingId)
                .washer(washer)
                .washBay(booking.getAssignedBay())
                .startTime(LocalDateTime.now())
                .status("ACTIVE")
                .build();

        assignmentRepository.save(assignment);

        return WashAssignmentResponse.builder()
                .bookingId(bookingId)
                .washerId(washer.getId())
                .washBayId(booking.getAssignedBay().getId())
                .status("ACTIVE")
                .build();
    }

    @Override
    public List<WashAssignmentResponse> getAssignmentsByWasher(Long washerId, String date) {
        return List.of();
    }

    @Override
    public void autoAssignWashersForShift(String date, String shiftType) {

    }
    @Override
    @Transactional
    public void autoAssignWasher(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Chưa có bay thì không gán washer
        if (booking.getAssignedBay() == null) {
            return;
        }

        // Lấy danh sách washer đang rảnh
        List<Washer> washers = washerRepository.findAvailableWashers();

        if (washers.isEmpty()) {

            System.out.println("No available washer.");

            return;
        }

        // Gán washer đầu tiên
        assignWasherToBooking(
                bookingId,
                washers.getFirst().getId()
        );
    }


}