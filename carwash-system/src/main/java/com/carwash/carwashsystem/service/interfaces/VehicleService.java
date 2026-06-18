package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.VehicleRequest;
import com.carwash.carwashsystem.dto.response.VehicleResponse;
import java.util.List;

public interface VehicleService {
    List<VehicleResponse> getVehiclesByCustomerId(Long customerId);

    VehicleResponse addVehicle(Long customerId, VehicleRequest request);
    VehicleResponse updateVehicle(Long vehicleId, VehicleRequest request);
    void deleteVehicle(Long vehicleId);
    List<VehicleResponse> getVehiclesByCustomer(Long customerId);
    VehicleResponse getVehicleById(Long vehicleId);
}