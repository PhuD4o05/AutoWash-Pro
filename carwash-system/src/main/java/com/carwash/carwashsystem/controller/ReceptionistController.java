package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.ExtraServiceRequest;
import com.carwash.carwashsystem.dto.request.WalkinBookingRequest;
import com.carwash.carwashsystem.dto.response.CheckinInfoResponse;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.WashQueue;
import com.carwash.carwashsystem.service.interfaces.ReceptionistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receptionist")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
@Tag(name = "Receptionist Controller", description = "Nghiệp vụ tiếp tân")
public class ReceptionistController {

    private final ReceptionistService receptionistService;

    @PostMapping("/checkin/qr")
    public ResponseEntity<CheckinInfoResponse> scanQR(@RequestParam String qrCode) {
        return ResponseEntity.ok(receptionistService.scanQRCheckin(qrCode));
    }

    @PostMapping("/checkin/manual")
    public ResponseEntity<CheckinInfoResponse> manualCheckin(@RequestParam String phone) {
        return ResponseEntity.ok(receptionistService.manualCheckinByPhone(phone));
    }

    @PostMapping("/walkin")
    public ResponseEntity<CheckinInfoResponse> walkinBooking(@Valid @RequestBody WalkinBookingRequest request) {
        return ResponseEntity.ok(receptionistService.createWalkinBooking(request));
    }

    @GetMapping("/today-bookings")
    public ResponseEntity<List<Booking>> getTodayBookings() {
        return ResponseEntity.ok(receptionistService.getTodayBookings());
    }

    @GetMapping("/queue")
    public ResponseEntity<List<WashQueue>> getQueue() {
        return ResponseEntity.ok(receptionistService.getCurrentQueue());
    }

    @PostMapping("/confirm-payment/{bookingId}")
    public ResponseEntity<Void> confirmPayment(@PathVariable Long bookingId, @RequestParam String paymentMethod) {
        receptionistService.confirmPayment(bookingId, paymentMethod);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{bookingId}/extra-service")
    public ResponseEntity<Booking> addExtraService(
            @PathVariable Long bookingId,
            @RequestBody ExtraServiceRequest request
    ){

        return ResponseEntity.ok(
                receptionistService.addExtraService(
                        bookingId,
                        request
                )
        );
    }
}
