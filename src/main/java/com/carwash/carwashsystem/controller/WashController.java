package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.AssignBayRequest;
import com.carwash.carwashsystem.dto.response.WashBayResponse;
import com.carwash.carwashsystem.dto.response.WasherResponse;
import com.carwash.carwashsystem.service.interfaces.WashBayService;
import com.carwash.carwashsystem.service.interfaces.WasherService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wash")
@RequiredArgsConstructor
@Tag(name = "Wash Controller", description = "Quản lý khu vực rửa xe và nhân viên rửa")
public class WashController {

    private final WashBayService washBayService;
    private final WasherService washerService;

    @GetMapping("/bays")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'WASHER')")
    public ResponseEntity<List<WashBayResponse>> getAllBays() {
        return ResponseEntity.ok(washBayService.getAllWashBays());  // ✅ sửa: gọi đúng method
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<WashBayResponse> assignBay(@Valid @RequestBody AssignBayRequest request) {
        return ResponseEntity.ok(washBayService.assignBayToBooking(request));
    }

    @PutMapping("/release/{bayId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Void> releaseBay(@PathVariable Long bayId) {
        washBayService.releaseBay(bayId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/washers")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<List<WasherResponse>> getActiveWashers() {
        return ResponseEntity.ok(washerService.getAllActiveWashers());
    }
}