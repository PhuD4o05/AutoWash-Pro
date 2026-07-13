package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.VehicleRequest;
import com.carwash.carwashsystem.dto.response.VehicleResponse;
import com.carwash.carwashsystem.service.interfaces.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // Danh sách xe của khách đang đăng nhập
    @GetMapping("/my-vehicles")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<VehicleResponse>> getMyVehicles() {
        Long customerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(vehicleService.getVehiclesByCustomerId(customerId));
    }

    // Thêm xe cho khách đang đăng nhập
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<VehicleResponse> addVehicle(@Valid @RequestBody VehicleRequest request) {
        Long customerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(vehicleService.addVehicle(customerId, request));
    }

    // Sửa xe
    @PutMapping("/{vehicleId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<VehicleResponse> updateVehicle(@PathVariable Long vehicleId,
                                                         @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(vehicleId, request));
    }

    // Xoá xe
    @DeleteMapping("/{vehicleId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }
}