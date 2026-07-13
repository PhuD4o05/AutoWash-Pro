package com.carwash.carwashsystem.service.impl;


import com.carwash.carwashsystem.dto.request.ExtraServiceRequest;
import com.carwash.carwashsystem.dto.response.BookingResponse;
import com.carwash.carwashsystem.entity.*;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.BookingExtraServiceRepository;
import com.carwash.carwashsystem.service.interfaces.ReceptionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReceptionServiceImpl implements ReceptionService {


    private final BookingRepository bookingRepository;

    private final BookingExtraServiceRepository extraRepository;



    @Override
    @Transactional
    public BookingResponse addExtraService(
            Long bookingId,
            ExtraServiceRequest request
    ){


        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found"));



        BookingExtraService extra =
                BookingExtraService.builder()
                        .booking(booking)
                        .serviceName(request.getServiceName())
                        .price(request.getPrice())
                        .build();



        extraRepository.save(extra);



        Double current =
                booking.getTotalPrice() == null
                        ? 0
                        : booking.getTotalPrice();



        booking.setTotalPrice(
                current + request.getPrice()
        );


        bookingRepository.save(booking);



        return null;
    }

}