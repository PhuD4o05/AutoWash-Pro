package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.response.ServicePackageResponse;
import com.carwash.carwashsystem.entity.ServicePackage;
import java.util.List;

public interface ServicePackageService {
    List<ServicePackageResponse> getAllActivePackages();

    // Lấy tất cả (cho admin)
    List<ServicePackage> getAllPackages();

    // Lấy gói đang hoạt động (cho khách)
    List<ServicePackage> getActivePackages();

    // Lấy chi tiết
    ServicePackage getPackageById(Long id);

    // Tạo mới
    ServicePackage createPackage(ServicePackage servicePackage);

    // Cập nhật
    ServicePackage updatePackage(Long id, ServicePackage updatedPackage);

    // Xóa
    void deletePackage(Long id);
}