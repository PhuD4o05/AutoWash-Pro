package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.AssignBayRequest;
import com.carwash.carwashsystem.dto.response.WashBayResponse;
import com.carwash.carwashsystem.entity.WashBay;

import java.util.List;

public interface WashBayService {
    List<WashBay> getAllBays();

    List<WashBayResponse> getAllWashBays();

    WashBayResponse getWashBayById(Long id);

    WashBayResponse updateWashBayStatus(Long id, String status);

    WashBayResponse assignBayToBooking(AssignBayRequest request);
    void releaseBay(Long bayId);

}