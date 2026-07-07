package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.response.QueueStatusResponse;
import com.carwash.carwashsystem.entity.WashQueue;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.repository.WashQueueRepository;
import com.carwash.carwashsystem.service.interfaces.QueueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private final WashQueueRepository queueRepository;

    @Override
    public void addToQueue(Long bookingId) {

    }

    @Override
    public void removeFromQueue(Long bookingId) {

    }

    @Override
    public QueueStatusResponse getQueuePosition(Long bookingId) {
        return null;
    }

    @Override
    public List<QueueStatusResponse> getCurrentQueue() {
        List<WashQueue> queues = queueRepository.findByStatusInOrderByQueuePositionAsc(
                List.of(BookingStatus.WAITING, BookingStatus.WASHING, BookingStatus.CANCELLED)
        );
        return queues.stream().map(q -> QueueStatusResponse.builder()
                .bookingId(q.getId())
                .status(q.getStatus().name())
                .queuePosition(q.getQueuePosition())
                .build()).collect(Collectors.toList());
    }

    @Override
    public void moveToNextInQueue(Long bayId) {

    }

    @Transactional
    @Override
    public void advanceQueue(Long bookingId) {
        WashQueue queueItem = queueRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Not in queue"));
        if (queueItem.getStatus() == BookingStatus.WAITING) {
            queueItem.setStatus(BookingStatus.WASHING);
        } else if (queueItem.getStatus() == BookingStatus.WASHING) {
            queueItem.setStatus(BookingStatus.CANCELLED);
        } else if (queueItem.getStatus() == BookingStatus.CANCELLED) {
            queueItem.setStatus(BookingStatus.COMPLETED);
            queueRepository.delete(queueItem);
        } else {
            throw new RuntimeException("Invalid status transition");
        }
        queueRepository.save(queueItem);
    }
}
