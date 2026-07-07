package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long customerId;
    private Long vehicleId;
    private Long packageId;
    private LocalDateTime scheduledTime;
    private BookingStatus status;
    private Double totalPrice;
    private String qrCode;
}