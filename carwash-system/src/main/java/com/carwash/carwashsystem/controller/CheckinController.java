package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.CheckinRequest;
import com.carwash.carwashsystem.dto.response.CheckinInfoResponse;
import com.carwash.carwashsystem.service.interfaces.CheckinService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECEPTIONIST')")
@Tag(name = "Checkin Controller", description = "Check-in xe")
public class CheckinController {

    private final CheckinService checkinService;

    @PostMapping("/qr")
    public ResponseEntity<CheckinInfoResponse> checkinByQR(@RequestParam String qrCode) {
        return ResponseEntity.ok(checkinService.scanQRCode(qrCode));
    }

    @PostMapping("/walkin")
    public ResponseEntity<CheckinInfoResponse> walkinCheckin(@RequestParam String phone,
                                                             @Valid @RequestBody CheckinRequest request) {
        return ResponseEntity.ok(checkinService.walkinCheckin(phone, request));
    }
}