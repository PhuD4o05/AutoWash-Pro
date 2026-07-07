package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.response.ServicePackageResponse;
import com.carwash.carwashsystem.entity.ServicePackage;
import com.carwash.carwashsystem.repository.ServicePackageRepository;
import com.carwash.carwashsystem.service.interfaces.ServicePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicePackageServiceImpl implements ServicePackageService {

    private final ServicePackageRepository repository;

    @Override
    public List<ServicePackageResponse> getAllActivePackages() {
        return repository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServicePackageResponse getPackageById(Long id) {
        ServicePackage sp = repository.findById(id).orElseThrow();
        return toResponse(sp);
    }

    private ServicePackageResponse toResponse(ServicePackage sp) {
        return ServicePackageResponse.builder()
                .id(sp.getId())
                .name(sp.getName())
                .description(sp.getDescription())
                .estimatedMinutes(sp.getEstimatedMinutes())
                .basePrice(sp.getBasePrice())
                .vehicleType(sp.getVehicleType())
                .isActive(sp.getIsActive())
                .build();
    }
}