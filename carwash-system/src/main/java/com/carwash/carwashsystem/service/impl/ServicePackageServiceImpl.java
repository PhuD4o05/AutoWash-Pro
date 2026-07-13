package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.response.ServicePackageResponse;
import com.carwash.carwashsystem.entity.ServicePackage;
import com.carwash.carwashsystem.repository.ServicePackageRepository;
import com.carwash.carwashsystem.repository.WashBayRepository;
import com.carwash.carwashsystem.service.interfaces.ServicePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicePackageServiceImpl implements ServicePackageService {

    private final ServicePackageRepository servicePackageRepository;
    private final WashBayRepository washBayRepository;

    @Override
    public List<ServicePackageResponse> getAllActivePackages() {
        return servicePackageRepository.findByIsActiveTrue()
                .stream()
                .map(sp -> ServicePackageResponse.builder()
                        .id(sp.getId())
                        .name(sp.getName())
                        .description(sp.getDescription())
                        .estimatedMinutes(sp.getEstimatedMinutes())
                        .basePrice(sp.getBasePrice())
                        .vehicleType(sp.getVehicleType())
                        .isActive(sp.getIsActive())
                        .build())
                .toList();
    }

    @Override
    public List<ServicePackage> getAllPackages() {
        return servicePackageRepository.findAll();
    }

    @Override
    public List<ServicePackage> getActivePackages() {
        return servicePackageRepository.findByIsActiveTrue();
    }

    @Override
    public ServicePackage getPackageById(Long id) {
        return servicePackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói dịch vụ với ID: " + id));
    }

    @Override
    public ServicePackage createPackage(ServicePackage servicePackage) {
        return servicePackageRepository.save(servicePackage);
    }

    @Override
    public ServicePackage updatePackage(Long id, ServicePackage updatedPackage) {
        ServicePackage existing = getPackageById(id);
        existing.setName(updatedPackage.getName());
        existing.setDescription(updatedPackage.getDescription());
        existing.setBasePrice(updatedPackage.getBasePrice());
        existing.setEstimatedMinutes(updatedPackage.getEstimatedMinutes());
        existing.setIsActive(updatedPackage.getIsActive());
        existing.setVehicleType(updatedPackage.getVehicleType());
        return servicePackageRepository.save(existing);
    }

    @Override
    public void deletePackage(Long id) {
        servicePackageRepository.deleteById(id);
    }
}