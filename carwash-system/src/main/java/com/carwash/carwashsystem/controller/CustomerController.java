package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.ChangePasswordRequest;
import com.carwash.carwashsystem.dto.request.CustomerUpdateRequest;
import com.carwash.carwashsystem.dto.response.CustomerResponse;
import com.carwash.carwashsystem.service.interfaces.AuthenticationService;
import com.carwash.carwashsystem.service.interfaces.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Controller", description = "Quản lý thông tin khách hàng")
public class CustomerController {

    private final CustomerService customerService;
    private final AuthenticationService authenticationService;

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
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }
//    @PutMapping("/me/password")
//    @Operation(summary = "Đổi mật khẩu của tài khoản hiện tại")
//    //@PreAuthorize("hasRole('CUSTOMER')")  //  Nên thêm để đảm bảo chỉ customer mới được đổi
//    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN')") // nếu admin cũng có thể đổi
//    public ResponseEntity<Void> changePassword(
//            @Valid @RequestBody ChangePasswordRequest request,
//            Authentication authentication) {
//
//        String username = authentication.getName(); // lấy email hoặc phone
//        authenticationService.changePassword(username, request.getOldPassword(), request.getNewPassword()); //  SỬA LẠI
//        return ResponseEntity.ok().build();
//    }
@PutMapping("/me/password")
@Operation(summary = "Đổi mật khẩu của tài khoản hiện tại")
@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
public ResponseEntity<Void> changePassword(
        @Valid @RequestBody ChangePasswordRequest request,
        Authentication authentication) {

    String username = authentication.getName();
    authenticationService.changePassword(
            username,
            request.getOldPassword(),
            request.getNewPassword());

    return ResponseEntity.ok().build();
}
}
