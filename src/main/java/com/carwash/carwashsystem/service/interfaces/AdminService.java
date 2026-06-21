package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.*;
import com.carwash.carwashsystem.dto.response.*;
import java.util.List;

public interface AdminService {

    // Quản lý tài khoản
    void createCustomerAccount(RegisterRequest request);
    void createReceptionistAccount(String fullName, String email, String phone, String password);
    void createWasherAccount(String fullName, String email, String phone, String password);
    void deactivateUser(Long userId, String role);
    void activateUser(Long userId, String role);

    // Quản lý gói dịch vụ
    ServicePackageResponse createServicePackage(ServicePackageRequest request);
    ServicePackageResponse updateServicePackage(Long id, ServicePackageRequest request);
    void deleteServicePackage(Long id);

    // Quản lý giá động
    DynamicPriceRuleResponse createPriceRule(PriceRuleRequest request);
    void deletePriceRule(Long id);

    // Quản lý khuyến mãi
    PromotionResponse createPromotion(PromotionRequest request);
    void deletePromotion(Long id);

    // Quản lý ca làm việc
    List<WorkShiftResponse> getAllWorkShifts();
    WorkShiftResponse assignWorkShift(ShiftAssignmentRequest request);

    // Quản lý Wash Bay
    List<WashBayResponse> getAllWashBays();
    WashBayResponse updateWashBayStatus(Long bayId, String status);
}