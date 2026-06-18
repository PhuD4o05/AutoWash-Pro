package com.carwash.carwashsystem.websocket;

import com.carwash.carwashsystem.dto.response.LiveTrackingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WashStatusPublisher {
    private final LiveTrackingWebSocketHandler webSocketHandler;
    private static final String DESTINATION = "/topic/wash-status";

    public void publishStatusUpdate(LiveTrackingResponse status) {
        webSocketHandler.broadcastMessage(DESTINATION, status);
        log.debug("Published wash status update for booking: {}", status.getBookingId());
    }

    public void publishQueueUpdate(Object queueData) {
        webSocketHandler.broadcastMessage("/topic/queue", queueData);
    }
}