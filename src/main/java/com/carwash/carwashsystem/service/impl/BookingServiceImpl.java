package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.BookingRequest;
import com.carwash.carwashsystem.dto.response.BookingResponse;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.entity.ServicePackage;
import com.carwash.carwashsystem.entity.Vehicle;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.exception.BookingConflictException;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.repository.ServicePackageRepository;
import com.carwash.carwashsystem.repository.VehicleRepository;
import com.carwash.carwashsystem.service.interfaces.BookingService;
import com.carwash.carwashsystem.service.interfaces.EmailService;
import com.carwash.carwashsystem.service.interfaces.PricingService;
import com.carwash.carwashsystem.service.interfaces.QRCodeService;
import org.springframework.beans.factory.annotation.Value;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ServicePackageRepository packageRepository;
    private final PricingService pricingService;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        ServicePackage servicePackage = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Service package not found"));

        if (!checkSlotAvailable(request.getScheduledTime(), request.getPackageId())) {
            throw new BookingConflictException("Time slot not available");
        }

        Double finalPrice = pricingService.calculateFinalPrice(
                request.getPackageId(),
                request.getScheduledTime(),
                customer.getMembershipTier().name(),
                null
        );

        String qrCode = qrCodeService.generateQRCodeForBooking(UUID.randomUUID().toString());

        // Sửa: gán đối tượng, không dùng ID trực tiếp
        Booking booking = Booking.builder()
                .customer(customer)
                .vehicle(vehicle)
                .servicePackage(servicePackage)
                .scheduledTime(request.getScheduledTime())
                .status(BookingStatus.PENDING)
                .totalPrice(finalPrice != null ? finalPrice : 0.0)
                .qrCode(qrCode)
                .build();

        Booking saved = bookingRepository.save(booking);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, Long customerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Not authorized");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public BookingResponse rescheduleBooking(Long bookingId, LocalDateTime newTime, Long customerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Not authorized");
        }
        if (!checkSlotAvailable(newTime, booking.getServicePackage().getId())) {
            throw new BookingConflictException("New time slot not available");
        }
        booking.setScheduledTime(newTime);
        booking.setStatus(BookingStatus.PENDING);
        Booking updated = bookingRepository.save(booking);
        return mapToResponse(updated);
    }

    @Override
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByCustomer(Long customerId) {
        // Repository cần có method findByCustomer(Customer customer) hoặc dùng @Query
        // Tạm thời dùng: customerRepository.findById(customerId) rồi tìm booking
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return bookingRepository.findByCustomer(customer).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse updateBookingStatus(Long bookingId, BookingStatus status, Long actorId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setStatus(status);
        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public boolean checkSlotAvailable(LocalDateTime scheduledTime, Long servicePackageId) {
        long count = bookingRepository.countActiveBookingsInPeriod(scheduledTime.minusMinutes(30), scheduledTime.plusMinutes(30));
        return count < 5;
    }

    @Override
    public List<LocalDateTime> getAvailableSlots(LocalDateTime date, Long servicePackageId) {
        return List.of();
    }

    @Override
    public List<BookingResponse> getTodayBookings() {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime end = start.plusDays(1);
        return bookingRepository.findByScheduledTimeBetween(start, end).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer() != null ? booking.getCustomer().getId() : null)
                .vehicleId(booking.getVehicle() != null ? booking.getVehicle().getId() : null)
                .packageId(booking.getServicePackage() != null ? booking.getServicePackage().getId() : null)
                .scheduledTime(booking.getScheduledTime())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .qrCode(booking.getQrCode())
                .build();
    }
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
}