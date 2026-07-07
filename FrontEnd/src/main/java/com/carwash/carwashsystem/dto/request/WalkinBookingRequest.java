package com.carwash.carwashsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkinBookingRequest {
    @NotBlank
    private String phone;
    private String fullName; // nếu tạo mới tài khoản
    private Long vehicleId;   // nếu có xe cũ
    private String licensePlate; // nếu xe mới
    private String brand;
    private String model;
    private String color;
    @NotNull
    private Long packageId;
}