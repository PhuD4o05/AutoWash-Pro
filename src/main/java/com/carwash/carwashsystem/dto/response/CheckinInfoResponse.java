package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CheckinInfoResponse {
    private Long bookingId;
    private Long customerId;
    private Long vehicleId;
    private Long servicePackageId;
    private LocalDateTime scheduledTime;
    private BookingStatus status;
    private Integer queuePosition;
}