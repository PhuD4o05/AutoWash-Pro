package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.response.LiveTrackingResponse;
import com.carwash.carwashsystem.service.interfaces.LiveTrackingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/live-tracking")
@RequiredArgsConstructor
@Tag(name = "Live Tracking Controller", description = "Theo dõi trạng thái rửa xe realtime")
public class LiveTrackingController {

    private final LiveTrackingService liveTrackingService;

    @GetMapping
    public ResponseEntity<List<LiveTrackingResponse>> getCurrentStatus() {
        return ResponseEntity.ok(liveTrackingService.getTodayQueue());
    }
}
