package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveTrackingResponse {
    private Long bookingId;
    private String vehicleLicensePlate; // có thể cần join thêm
    private String serviceName;
    private BookingStatus status;
    private String washerName;
}