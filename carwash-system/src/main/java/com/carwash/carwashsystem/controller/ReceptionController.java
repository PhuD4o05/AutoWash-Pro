package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.ExtraServiceRequest;
import com.carwash.carwashsystem.dto.response.BookingResponse;
import com.carwash.carwashsystem.dto.response.PaymentResponse;
import com.carwash.carwashsystem.service.interfaces.BookingService;
import com.carwash.carwashsystem.service.interfaces.PaymentService;
import com.carwash.carwashsystem.service.interfaces.ReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reception")
@RequiredArgsConstructor
public class ReceptionController {

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final com.carwash.carwashsystem.service.interfaces.CustomerService customerService;
    private final com.carwash.carwashsystem.repository.WashBayRepository washBayRepository;
    private final ReceptionService receptionService;

    @GetMapping("/bookings")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
    public ResponseEntity<List<BookingResponse>> getBookings() {
        return ResponseEntity.ok(bookingService.getReceptionBookings());
    }

    @PutMapping("/bookings/{id}/checkin")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
    public ResponseEntity<BookingResponse> checkin(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.checkinBooking(id));
    }

    @PostMapping("/bookings/{id}/confirm-payment")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
    public ResponseEntity<PaymentResponse> confirmPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.confirmCashPayment(id, null));
    }
    // Tra khách theo SĐT
    @GetMapping("/customer")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
    public ResponseEntity<com.carwash.carwashsystem.dto.response.CustomerResponse> lookupCustomer(@RequestParam String phone) {
        return ResponseEntity.ok(customerService.getCustomerByPhone(phone));
    }

    // Danh sách bay
    @GetMapping("/bays")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getBays() {
        java.util.List<java.util.Map<String, Object>> result = washBayRepository.findAll().stream()
                .map(b -> {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", b.getId());
                    m.put("bayNumber", b.getBayNumber());
                    m.put("status", b.getStatus());
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // Gán bay cho đơn
    @PutMapping("/bookings/{id}/bay")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
    public ResponseEntity<BookingResponse> assignBay(@PathVariable Long id, @RequestParam Long bayId) {
        return ResponseEntity.ok(bookingService.assignBay(id, bayId));
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
    public ResponseEntity<java.util.List<com.carwash.carwashsystem.dto.response.CustomerResponse>> getCustomers() {
        return ResponseEntity.ok(customerService.getCustomerAccounts());
    }

    @PostMapping("/customers")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
    public ResponseEntity<com.carwash.carwashsystem.dto.response.CustomerResponse> createCustomer(@RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(customerService.createCustomerByStaff(body.get("fullName"), body.get("phone"), body.get("password")));
    }

    @PutMapping("/customers/{id}/reset-password")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        customerService.resetPassword(id, body.get("password"));
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{bookingId}/extra-service")
    public ResponseEntity<?> addExtraService(
            @PathVariable Long bookingId,
            @RequestBody ExtraServiceRequest request
    ) {

        return ResponseEntity.ok(
                receptionService.addExtraService(
                        bookingId,
                        request
                )
        );
    }
}