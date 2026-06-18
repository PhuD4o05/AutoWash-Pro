package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.response.LiveTrackingResponse;
import com.carwash.carwashsystem.entity.Assignment;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.WashQueue;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.repository.AssignmentRepository;
import com.carwash.carwashsystem.repository.WashQueueRepository;
import com.carwash.carwashsystem.service.interfaces.LiveTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiveTrackingServiceImpl implements LiveTrackingService {

    private final WashQueueRepository queueRepository;
    private final AssignmentRepository assignmentRepository;

    @Override
    public List<LiveTrackingResponse> getCurrentWashStatus() {
        List<WashQueue> activeQueue = queueRepository.findByStatusInOrderByQueuePositionAsc(
                List.of(BookingStatus.WASHING, BookingStatus.CANCELLED) // CANCELLED? chỉ nên WASHING/DRYING
        );
        return activeQueue.stream().map(q -> {
            Long bookingId = q.getBooking().getId();  //  sửa: lấy từ booking
            Assignment assignment = assignmentRepository.findActiveAssignmentByBookingId(bookingId).orElse(null);
            String washerName = (assignment != null && assignment.getWasher() != null)
                    ? assignment.getWasher().getFullName()
                    : "N/A";
            return LiveTrackingResponse.builder()
                    .bookingId(bookingId)
                    .status(q.getStatus())
                    .washerName(washerName)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public LiveTrackingResponse getWashStatusByBooking(Long bookingId) {
        return queueRepository.findByBookingId(bookingId)
                .map(q -> {
                    Assignment assignment = assignmentRepository.findActiveAssignmentByBookingId(bookingId).orElse(null);
                    return LiveTrackingResponse.builder()
                            .bookingId(bookingId)
                            .status(q.getStatus())
                            .washerName(assignment != null && assignment.getWasher() != null
                                    ? assignment.getWasher().getFullName() : "N/A")
                            .build();
                })
                .orElse(null);
    }

    @Override
    public List<LiveTrackingResponse> getTodayQueue() {
        // Lấy tất cả booking trong ngày đang ở trạng thái WAITING, WASHING, DRYING
        return queueRepository.findTodayActiveQueue().stream()
                .map(q -> {
                    Long bookingId = q.getBooking().getId();
                    Assignment assignment = assignmentRepository.findActiveAssignmentByBookingId(bookingId).orElse(null);
                    return LiveTrackingResponse.builder()
                            .bookingId(bookingId)
                            .status(q.getStatus())
                            .washerName(assignment != null && assignment.getWasher() != null
                                    ? assignment.getWasher().getFullName() : "N/A")
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateWashStatus(Long bookingId, String status, Long washerId) {
        // Tìm WashQueue theo bookingId
        WashQueue queue = queueRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("WashQueue not found"));
        queue.setStatus(BookingStatus.valueOf(status));
        queueRepository.save(queue);
        // Broadcast update
        broadcastStatusUpdate(bookingId);
    }

    @Override
    public void broadcastStatusUpdate(Long bookingId) {
        // Gửi qua WebSocket (giả sử có WebSocket publisher)
        // Ví dụ: webSocketPublisher.publishStatusUpdate(getWashStatusByBooking(bookingId));
    }
}