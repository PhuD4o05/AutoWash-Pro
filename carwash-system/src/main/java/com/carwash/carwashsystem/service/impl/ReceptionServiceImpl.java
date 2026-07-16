package com.carwash.carwashsystem.service.impl;


import com.carwash.carwashsystem.dto.request.ExtraServiceRequest;
import com.carwash.carwashsystem.dto.response.BookingResponse;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.BookingExtraService;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.BookingExtraServiceRepository;
import com.carwash.carwashsystem.repository.BookingRepository;
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
    ) {


        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found"
                                )
                        );



        // Tạo dịch vụ phát sinh
        BookingExtraService extra =
                BookingExtraService.builder()
                        .booking(booking)
                        .serviceName(request.getServiceName())
                        .price(request.getPrice())
                        .build();



        // lưu bảng booking_extra_services
        extraRepository.save(extra);



        // cập nhật list trong Booking
        booking.getExtraServices().add(extra);



        // lấy tổng tiền hiện tại
        long currentFinalAmount =
                booking.getFinalAmount() == null
                        ?
                        booking.getTotalPrice().longValue()
                        :
                        booking.getFinalAmount();



        // cộng thêm dịch vụ
        long newFinalAmount =
                currentFinalAmount + request.getPrice();



        booking.setFinalAmount(newFinalAmount);



        // tính tiền còn lại
        long deposit =
                booking.getDepositAmount() == null
                        ?
                        0L
                        :
                        booking.getDepositAmount();



        booking.setRemainingAmount(
                newFinalAmount - deposit
        );



        Booking saved =
                bookingRepository.save(booking);



        return BookingResponse.builder()
                .id(saved.getId())
                .totalPrice(saved.getTotalPrice())
                .status(saved.getStatus())
                .build();
    }

}