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
    private final com.carwash.carwashsystem.service.interfaces.CustomerService customerService;
    private final com.carwash.carwashsystem.service.interfaces.BookingService bookingService;
    private final com.carwash.carwashsystem.repository.BookingRepository bookingRepository;
    private final com.carwash.carwashsystem.repository.PaymentRepository paymentRepository;
    private final com.carwash.carwashsystem.repository.CustomerRepository customerRepository;
    private final com.carwash.carwashsystem.repository.PromotionRepository promotionRepository;

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

    // Danh sách tất cả tài khoản (trong bảng customers, mọi role)
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<com.carwash.carwashsystem.dto.response.CustomerResponse>> getAllUsers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Tất cả booking trong hệ thống
    @GetMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<com.carwash.carwashsystem.dto.response.BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getReceptionBookings());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.Map<String, Object>> getStats() {
        var bookings = bookingRepository.findAll();
        var payments = paymentRepository.findAll();
        long total = bookings.size();
        long washes = bookings.stream().filter(b -> b.getStatus() == com.carwash.carwashsystem.enums.BookingStatus.COMPLETED).count();
        long cancelled = bookings.stream().filter(b -> b.getStatus() == com.carwash.carwashsystem.enums.BookingStatus.CANCELLED || b.getStatus() == com.carwash.carwashsystem.enums.BookingStatus.NO_SHOW).count();
        long revenue = payments.stream().filter(p -> p.getStatus() == com.carwash.carwashsystem.enums.PaymentStatus.PAID).mapToLong(p -> p.getAmount() != null ? p.getAmount() : 0).sum();
        long customers = customerRepository.findAll().stream().filter(c -> c.getRole() == com.carwash.carwashsystem.enums.Role.CUSTOMER).count();
        double cancelRate = total > 0 ? Math.round((cancelled * 1000.0 / total)) / 10.0 : 0;
        java.util.Map<String, Object> m = new java.util.HashMap<>();
        m.put("revenue", revenue);
        m.put("washes", washes);
        m.put("customers", customers);
        m.put("cancelRate", cancelRate);
        return ResponseEntity.ok(m);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.carwash.carwashsystem.dto.response.CustomerResponse> updateUser(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(customerService.updateUserByAdmin(id, body.get("fullName"), body.get("phone"), body.get("email"), body.get("role")));
    }
    @GetMapping("/promotions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<com.carwash.carwashsystem.entity.Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionRepository.findAll());
    }
}