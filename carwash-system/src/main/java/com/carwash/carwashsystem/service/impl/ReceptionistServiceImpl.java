package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.WalkinBookingRequest;
import com.carwash.carwashsystem.dto.response.CheckinInfoResponse;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.entity.Vehicle;
import com.carwash.carwashsystem.entity.WashQueue;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.enums.PaymentMethod;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.repository.VehicleRepository;
import com.carwash.carwashsystem.repository.WashQueueRepository;
import com.carwash.carwashsystem.service.interfaces.BookingService;
import com.carwash.carwashsystem.service.interfaces.PaymentService;
import com.carwash.carwashsystem.service.interfaces.QueueService;
import com.carwash.carwashsystem.service.interfaces.ReceptionistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceptionistServiceImpl implements ReceptionistService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final WashQueueRepository washQueueRepository;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final QueueService queueService;

    // Helper method để xử lý check-in chung, tránh duplicate code
    private CheckinInfoResponse performCheckin(Booking booking) {
        LocalDateTime now = LocalDateTime.now();
        if (booking.getScheduledTime().minusMinutes(30).isAfter(now)) {
            throw new RuntimeException("Too early for check-in");
        }
        if (booking.getScheduledTime().plusMinutes(15).isBefore(now)) {
            booking.setStatus(BookingStatus.NO_SHOW);
            bookingRepository.save(booking);
            throw new RuntimeException("Check-in time expired");
        }
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setCheckinTime(now);
        bookingRepository.save(booking);
        queueService.addToQueue(booking.getId());
        return CheckinInfoResponse.builder()
                .bookingId(booking.getId())
                .customerName(booking.getCustomer().getFullName())
                .phoneNumber(booking.getCustomer().getPhoneNumber())
                .vehicle(booking.getVehicle().getLicensePlate()) // giả sử có
                .servicePackage(booking.getServicePackage().getName())
                .totalPrice(Double.valueOf(booking.getServicePackage().getBasePrice()))
                .scheduledTime(booking.getScheduledTime())
                .checkinTime(booking.getCheckinTime()) // đã được set
                .status(booking.getStatus())
                .queuePosition(queueService.getQueuePosition(booking.getId()).getQueuePosition()) // nếu trả về Integer
                .message("Check-in thành công")
                .bayNumber(null)
                .build();
    }

    @Override
    @Transactional
    public CheckinInfoResponse scanQRCheckin(String qrCode) {
        Booking booking = bookingRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR code"));
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Booking not confirmed");
        }
        return performCheckin(booking);
    }

    @Override
    @Transactional
    public CheckinInfoResponse manualCheckinByPhone(String phone) {
        Customer customer = customerRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        // Tìm booking CONFIRMED trong ngày của customer
        List<Booking> bookings = bookingRepository.findByCustomerId(
                customer.getId());
        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("No confirmed booking found for today");
        }
        Booking booking = bookings.get(0); // Lấy booking đầu tiên
        return performCheckin(booking);
    }

    @Override
    @Transactional
    public CheckinInfoResponse createWalkinBooking(WalkinBookingRequest request) {
        Customer customer = customerRepository.findByPhoneNumber(request.getPhone())
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .phoneNumber(request.getPhone())
                            .fullName(request.getFullName() != null ? request.getFullName() : "Walk-in Customer")
                            .role(com.carwash.carwashsystem.enums.Role.CUSTOMER)
                            .membershipTier(com.carwash.carwashsystem.enums.MembershipTier.MEMBER)
                            .totalPoints(0)
                            .currentPoints(0)
                            .isActive(true)
                            .build();
                    return customerRepository.save(newCustomer);
                });
        Vehicle vehicle = vehicleRepository.findByLicensePlate(request.getLicensePlate())
                .orElseGet(() -> {
                    Vehicle newVehicle = Vehicle.builder()
                            .licensePlate(request.getLicensePlate())
                            .brand(request.getBrand())
                            .model(request.getModel())
                            .color(request.getColor())
                            .customer(customer)
                            .build();
                    return vehicleRepository.save(newVehicle);
                });
        // Tạo booking request
        var bookingRequest = new com.carwash.carwashsystem.dto.request.BookingRequest();
        bookingRequest.setVehicleId(vehicle.getId());
        bookingRequest.setPackageId(request.getPackageId());
        bookingRequest.setScheduledTime(LocalDateTime.now());
        var bookingResponse = bookingService.createBooking(bookingRequest, customer.getId());
        Booking booking = bookingRepository.findById(bookingResponse.getId()).orElseThrow();
        // Check-in ngay (bỏ qua kiểm tra thời gian vì walk-in)
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setCheckinTime(LocalDateTime.now());
        bookingRepository.save(booking);
        queueService.addToQueue(booking.getId());
        return CheckinInfoResponse.builder()
                .bookingId(booking.getId())
                .customerName(booking.getCustomer().getFullName())
                .phoneNumber(booking.getCustomer().getPhoneNumber())
                .vehicle(booking.getVehicle().getLicensePlate()) // giả sử có
                .servicePackage(booking.getServicePackage().getName())
                .totalPrice(Double.valueOf(booking.getServicePackage().getBasePrice()))
                .scheduledTime(booking.getScheduledTime())
                .checkinTime(booking.getCheckinTime()) // đã được set
                .status(booking.getStatus())
                .queuePosition(queueService.getQueuePosition(booking.getId()).getQueuePosition()) // nếu trả về Integer
                .message("Check-in thành công")
                .bayNumber(null)
                .build();
    }
    @Override
    public List<Booking> getTodayBookings() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return bookingRepository.findByScheduledTimeBetween(start, end);
    }

    @Override
    public List<WashQueue> getCurrentQueue() {
        return washQueueRepository.findByStatusInOrderByQueuePositionAsc(
                List.of(
                        BookingStatus.WAITING,
                        BookingStatus.WASHING
                )
        );
    }

    @Override
    @Transactional
    public void confirmPayment(Long bookingId, String paymentMethod) {
        PaymentMethod method = PaymentMethod.valueOf(paymentMethod.toUpperCase());
        if (method == PaymentMethod.CASH) {
            paymentService.confirmCashPayment(bookingId, null);
        } else {
            throw new RuntimeException("Only cash payment can be confirmed at counter");
        }
        // Có thể cập nhật trạng thái booking nếu cần
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            // Điểm loyalty đã được cộng khi washer hoàn thành
        }
    }
}