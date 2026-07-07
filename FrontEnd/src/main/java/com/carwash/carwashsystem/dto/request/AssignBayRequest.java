package com.carwash.carwashsystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignBayRequest {
    @NotNull
    private Long bayId;
    @NotNull
    private Long bookingId;
    @NotNull
    private Long washerId;
}