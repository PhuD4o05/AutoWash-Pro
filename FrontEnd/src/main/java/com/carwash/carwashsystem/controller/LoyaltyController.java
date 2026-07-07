package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.response.LoyaltyResponse;
import com.carwash.carwashsystem.service.interfaces.LoyaltyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
@Tag(name = "Loyalty Controller", description = "Quản lý điểm thưởng và hạng thành viên")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<LoyaltyResponse> getMyLoyalty() {
        Long customerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(loyaltyService.getCustomerLoyalty(customerId));
    }

    @PostMapping("/redeem")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> redeemPoints(@RequestParam int points, @RequestParam String voucherCode) {
        Long customerId = SecurityUtils.getCurrentUserId();
        loyaltyService.getCustomerLoyalty(customerId);
        return ResponseEntity.ok().build();
    }
}
