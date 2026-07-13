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

    private String packageName;
    private String licensePlate;
    private String carBrand;
    private String carModel;
    private String customerName;
    private String note;
    private String bayNumber;
    private Boolean paid;

    private Long depositAmount;

    private Long remainingAmount;


    private Boolean deposited;



    private Long remainAmount;
}