package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.BookingRequest;
import com.carwash.carwashsystem.dto.response.BookingResponse;
import com.carwash.carwashsystem.service.interfaces.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking Controller", description = "Quản lý đặt lịch")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        Long customerId = SecurityUtils.getCurrentUserId();
        // Sửa thứ tự tham số: request trước, customerId sau
        return ResponseEntity.ok(bookingService.createBooking(request, customerId));
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        Long customerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(bookingService.getBookingsByCustomer(customerId));
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        Long customerId = SecurityUtils.getCurrentUserId();
        bookingService.cancelBooking(bookingId, customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<List<BookingResponse>> getTodayBookings() {
        return ResponseEntity.ok(bookingService.getTodayBookings());
    }
    @PostMapping("/{bookingId}/start")
    public ResponseEntity<BookingResponse> startWash(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.startWash(bookingId)
        );
    }
}