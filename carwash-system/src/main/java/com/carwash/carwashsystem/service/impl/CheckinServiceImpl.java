package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.BookingRequest;
import com.carwash.carwashsystem.dto.request.CheckinRequest;
import com.carwash.carwashsystem.dto.response.BookingResponse;
import com.carwash.carwashsystem.dto.response.CheckinInfoResponse;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.exception.BookingConflictException;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.service.interfaces.BookingService;
import com.carwash.carwashsystem.service.interfaces.CheckinService;
import com.carwash.carwashsystem.service.interfaces.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements CheckinService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final QueueService queueService;
    private final BookingService bookingService;

    @Override
    @Transactional
    public CheckinInfoResponse scanQRCode(String qrCode) {
        Booking booking = bookingRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR code"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingConflictException("Booking is not in confirmed status");
        }

        LocalDateTime now = LocalDateTime.now();
        if (booking.getScheduledTime().minusMinutes(30).isAfter(now)) {
            throw new BookingConflictException("Too early for check-in");
        }
        if (booking.getScheduledTime().plusMinutes(15).isBefore(now)) {
            booking.setStatus(BookingStatus.NO_SHOW);
            bookingRepository.save(booking);
            throw new BookingConflictException("Check-in time expired");
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setCheckinTime(now);
        bookingRepository.save(booking);

        queueService.addToQueue(booking.getId());

        return CheckinInfoResponse.builder()
                .bookingId(booking.getId())
                .customerId(booking.getId())
                .vehicleId(booking.getId())
                .servicePackageId(booking.getId())
                .scheduledTime(booking.getScheduledTime())
                .status(booking.getStatus())
                .queuePosition(queueService.getQueuePosition(booking.getId()).getPosition())
                .build();
    }

    @Override
    @Transactional
    public CheckinInfoResponse walkinCheckin(String phone, CheckinRequest request) {
        Customer customer = customerRepository.findByPhoneNumber(phone)
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .phoneNumber(phone)
                            .fullName("Khách lẻ")
                            .isActive(true)
                            .build();
                    return customerRepository.save(newCustomer);
                });

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setVehicleId(request.getVehicleId());
        bookingRequest.setPackageId(request.getPackageId());
        bookingRequest.setScheduledTime(LocalDateTime.now());

        BookingResponse bookingResp = bookingService.createBooking(bookingRequest, customer.getId());

        Booking booking = bookingRepository.findById(bookingResp.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setCheckinTime(LocalDateTime.now());
        bookingRepository.save(booking);

        queueService.addToQueue(booking.getId());

        return CheckinInfoResponse.builder()
                .bookingId(booking.getId())
                .customerId(customer.getId())
                .vehicleId(booking.getId())
                .servicePackageId(booking.getId())
                .scheduledTime(booking.getScheduledTime())
                .status(booking.getStatus())
                .queuePosition(queueService.getQueuePosition(booking.getId()).getPosition())
                .build();
    }
}