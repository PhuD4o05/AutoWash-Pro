package com.carwash.carwashsystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueueStatusResponse {
    private Long bookingId;
    private Integer position;
    private String status;
    private Integer queuePosition;
}