package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.response.QueueStatusResponse;
import com.carwash.carwashsystem.service.interfaces.QueueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
@Tag(name = "Queue Controller", description = "Quản lý hàng chờ")
public class QueueController {

    private final QueueService queueService;

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'WASHER', 'ADMIN')")
    public ResponseEntity<List<QueueStatusResponse>> getCurrentQueue() {
        return ResponseEntity.ok(queueService.getCurrentQueue());
    }

    @PutMapping("/advance/{bookingId}")
    @PreAuthorize("hasAnyRole('WASHER', 'ADMIN')")
    public ResponseEntity<Void> advanceQueue(@PathVariable Long bookingId) {
        queueService.addToQueue(bookingId);
        return ResponseEntity.ok().build();
    }
}
