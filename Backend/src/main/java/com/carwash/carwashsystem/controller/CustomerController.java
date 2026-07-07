package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.CustomerUpdateRequest;
import com.carwash.carwashsystem.dto.response.CustomerResponse;
import com.carwash.carwashsystem.service.interfaces.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Controller", description = "Quản lý thông tin khách hàng")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> getCurrentCustomer() {
        Long id = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> updateCurrentCustomer(@Valid @RequestBody CustomerUpdateRequest request) {
        Long id = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }
}
