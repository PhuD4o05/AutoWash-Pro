package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.response.QueueStatusResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface QueueService {
    void addToQueue(Long bookingId);
    void removeFromQueue(Long bookingId);
    QueueStatusResponse getQueuePosition(Long bookingId);
    List<QueueStatusResponse> getCurrentQueue();
    void moveToNextInQueue(Long bayId);

    @Transactional
    void advanceQueue(Long bookingId);
}