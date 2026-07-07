package com.carwash.carwashsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WashAssignmentResponse {
    private Long bookingId;
    private Long washBayId;
    private Long washerId;
    private String status; // ACTIVE, COMPLETED
}