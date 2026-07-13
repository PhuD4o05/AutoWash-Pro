package com.carwash.carwashsystem.service.impl;
import org.springframework.dao.DataIntegrityViolationException;
import com.carwash.carwashsystem.dto.request.VehicleRequest;
import com.carwash.carwashsystem.dto.response.VehicleResponse;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.entity.Vehicle;
import com.carwash.carwashsystem.exception.DuplicateResourceException;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.repository.VehicleRepository;
import com.carwash.carwashsystem.service.interfaces.VehicleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    private final CustomerRepository customerRepository;

    @Override
    public List<VehicleResponse> getVehiclesByCustomerId(Long customerId) {
        return vehicleRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleResponse addVehicle(Long customerId, VehicleRequest request) {
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new DuplicateResourceException("License plate already exists");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found"));


        Vehicle vehicle = Vehicle.builder()
                .customer(customer)
                .licensePlate(request.getLicensePlate())
                .brand(request.getBrand())
                .model(request.getModel())
                .color(request.getColor())
                .imageUrl(request.getImageUrl())
                .build();
        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long vehicleId, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        // Nếu đổi biển số: kiểm tra biển mới có bị xe khác dùng chưa
        String newPlate = request.getLicensePlate();
        if (newPlate != null && !newPlate.equals(vehicle.getLicensePlate())) {
            if (vehicleRepository.existsByLicensePlate(newPlate)) {
                throw new DuplicateResourceException("License plate already exists");
            }
            vehicle.setLicensePlate(newPlate);
        }

        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setColor(request.getColor());
        vehicle.setImageUrl(request.getImageUrl());
        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public void deleteVehicle(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle not found");
        }
        try {
            vehicleRepository.deleteById(vehicleId);
            vehicleRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Xe đang có lịch sử đặt lịch, không thể xoá.");
        }
    }

    @Override
    public List<VehicleResponse> getVehiclesByCustomer(Long customerId) {
        return List.of();
    }

    @Override
    public VehicleResponse getVehicleById(Long vehicleId) {
        return null;
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .color(vehicle.getColor())
                .imageUrl(vehicle.getImageUrl())
                .build();
    }
}