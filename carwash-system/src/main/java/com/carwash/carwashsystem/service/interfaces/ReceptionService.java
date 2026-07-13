package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.ExtraServiceRequest;
import com.carwash.carwashsystem.dto.response.BookingResponse;


public interface ReceptionService {


    BookingResponse addExtraService(
            Long bookingId,
            ExtraServiceRequest request
    );


}