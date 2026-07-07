package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.response.ServicePackageResponse;
import java.util.List;

public interface ServicePackageService {
    List<ServicePackageResponse> getAllActivePackages();
    ServicePackageResponse getPackageById(Long id);
}