package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.response.LiveTrackingResponse;
import java.util.List;

public interface LiveTrackingService {
    List<LiveTrackingResponse> getCurrentWashStatus();

    LiveTrackingResponse getWashStatusByBooking(Long bookingId);
    List<LiveTrackingResponse> getTodayQueue();
    void updateWashStatus(Long bookingId, String status, Long washerId);
    void broadcastStatusUpdate(Long bookingId);
}