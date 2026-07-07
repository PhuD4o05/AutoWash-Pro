package com.carwash.carwashsystem.dto.request;

import com.carwash.carwashsystem.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWashStatusRequest {
    @NotNull
    private Long bookingId;
    @NotNull
    private BookingStatus status; // WAITING, WASHING, DRYING, COMPLETED
}