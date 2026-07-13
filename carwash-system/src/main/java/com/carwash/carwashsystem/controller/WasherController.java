package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.response.BookingResponse;
import com.carwash.carwashsystem.service.interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/washer")
@RequiredArgsConstructor
public class WasherController {

    private final BookingService bookingService;

    // Danh sách xe đang cần xử lý (waiting/washing/checked-in/completed)
    @GetMapping("/jobs")
    @PreAuthorize("hasAnyRole('WASHER','ADMIN')")
    public ResponseEntity<List<BookingResponse>> getJobs() {
        return ResponseEntity.ok(bookingService.getActiveJobs());
    }

    // Chuyển trạng thái: Waiting -> Washing -> Completed
    @PutMapping("/jobs/{bookingId}/advance")
    @PreAuthorize("hasAnyRole('WASHER','ADMIN')")
    public ResponseEntity<BookingResponse> advance(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.advanceBookingStatus(bookingId));
    }

    // Lưu ghi chú cho 1 xe
    @PutMapping("/jobs/{bookingId}/note")
    @PreAuthorize("hasAnyRole('WASHER','ADMIN')")
    public ResponseEntity<BookingResponse> updateNote(@PathVariable Long bookingId,
                                                      @RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(bookingService.updateNote(bookingId, body.get("note")));
    }
}