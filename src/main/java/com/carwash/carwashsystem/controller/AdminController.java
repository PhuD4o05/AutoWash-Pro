package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.*;
import com.carwash.carwashsystem.dto.response.*;
import com.carwash.carwashsystem.service.interfaces.AdminService;
import com.carwash.carwashsystem.service.interfaces.ServicePackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Controller", description = "Quản lý hệ thống cho Admin")
public class AdminController {

    private final AdminService adminService;
    private final ServicePackageService servicePackageService;

    // ==================== Quản lý tài khoản ====================
    @PostMapping("/customers")
    @Operation(summary = "Tạo tài khoản khách hàng")
    public ResponseEntity<Void> createCustomer(@Valid @RequestBody RegisterRequest request) {
        adminService.createCustomerAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/receptionists")
    @Operation(summary = "Tạo tài khoản tiếp tân")
    public ResponseEntity<Void> createReceptionist(@RequestParam String fullName,
                                                   @RequestParam String email,
                                                   @RequestParam String phone,
                                                   @RequestParam String password) {
        adminService.createReceptionistAccount(fullName, email, phone, password);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/washers")
    @Operation(summary = "Tạo tài khoản nhân viên rửa xe")
    public ResponseEntity<Void> createWasher(@RequestParam String fullName,
                                             @RequestParam String email,
                                             @RequestParam String phone,
                                             @RequestParam String password) {
        adminService.createWasherAccount(fullName, email, phone, password);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/users/{userId}/{role}")
    @Operation(summary = "Vô hiệu hóa tài khoản")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId, @PathVariable String role) {
        adminService.deactivateUser(userId, role);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/{role}/activate")
    @Operation(summary = "Kích hoạt lại tài khoản")
    public ResponseEntity<Void> activateUser(@PathVariable Long userId, @PathVariable String role) {
        adminService.activateUser(userId, role);
        return ResponseEntity.noContent().build();
    }

    // ==================== Quản lý gói dịch vụ ====================
    @GetMapping("/packages")
    @Operation(summary = "Lấy danh sách gói dịch vụ đang hoạt động")
    public ResponseEntity<List<ServicePackageResponse>> getAllActivePackages() {
        return ResponseEntity.ok(servicePackageService.getAllActivePackages());
    }

    @PostMapping("/packages")
    @Operation(summary = "Tạo gói dịch vụ mới")
    public ResponseEntity<ServicePackageResponse> createPackage(@Valid @RequestBody ServicePackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createServicePackage(request));
    }

    @PutMapping("/packages/{id}")
    @Operation(summary = "Cập nhật gói dịch vụ")
    public ResponseEntity<ServicePackageResponse> updatePackage(@PathVariable Long id, @Valid @RequestBody ServicePackageRequest request) {
        return ResponseEntity.ok(adminService.updateServicePackage(id, request));
    }

    @DeleteMapping("/packages/{id}")
    @Operation(summary = "Xóa gói dịch vụ (vô hiệu hóa)")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        adminService.deleteServicePackage(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Quản lý giá động ====================
    @PostMapping("/price-rules")
    @Operation(summary = "Tạo quy tắc giá động")
    public ResponseEntity<DynamicPriceRuleResponse> createPriceRule(@Valid @RequestBody PriceRuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createPriceRule(request));
    }

    @DeleteMapping("/price-rules/{id}")
    @Operation(summary = "Xóa quy tắc giá động")
    public ResponseEntity<Void> deletePriceRule(@PathVariable Long id) {
        adminService.deletePriceRule(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Quản lý khuyến mãi ====================
    @PostMapping("/promotions")
    @Operation(summary = "Tạo chương trình khuyến mãi")
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody PromotionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createPromotion(request));
    }

    @DeleteMapping("/promotions/{id}")
    @Operation(summary = "Xóa chương trình khuyến mãi")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        adminService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Quản lý ca làm việc ====================
    @GetMapping("/shifts")
    @Operation(summary = "Lấy danh sách ca làm việc")
    public ResponseEntity<List<WorkShiftResponse>> getAllShifts() {
        return ResponseEntity.ok(adminService.getAllWorkShifts());
    }

    @PostMapping("/shifts/assign")
    @Operation(summary = "Phân công nhân viên vào ca")
    public ResponseEntity<WorkShiftResponse> assignShift(@Valid @RequestBody ShiftAssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.assignWorkShift(request));
    }

    // ==================== Quản lý Wash Bay ====================
    @GetMapping("/wash-bays")
    @Operation(summary = "Danh sách khu vực rửa xe")
    public ResponseEntity<List<WashBayResponse>> getAllWashBays() {
        return ResponseEntity.ok(adminService.getAllWashBays());
    }

    @PutMapping("/wash-bays/{bayId}/status")
    @Operation(summary = "Cập nhật trạng thái khu vực rửa xe")
    public ResponseEntity<WashBayResponse> updateBayStatus(@PathVariable Long bayId, @RequestParam String status) {
        return ResponseEntity.ok(adminService.updateWashBayStatus(bayId, status));
    }
}