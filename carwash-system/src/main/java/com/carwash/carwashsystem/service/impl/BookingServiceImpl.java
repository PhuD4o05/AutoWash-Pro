package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.BookingRequest;
import com.carwash.carwashsystem.dto.response.BookingResponse;
import com.carwash.carwashsystem.dto.response.CheckinInfoResponse;
import com.carwash.carwashsystem.entity.*;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.enums.PaymentMethod;
import com.carwash.carwashsystem.enums.PaymentType;
import com.carwash.carwashsystem.exception.BookingConflictException;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.*;
import com.carwash.carwashsystem.service.interfaces.*;
import org.springframework.beans.factory.annotation.Value;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.carwash.carwashsystem.repository.PaymentRepository;
import com.carwash.carwashsystem.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final WashBayRepository washBayRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ServicePackageRepository packageRepository;
    private final PricingService pricingService;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;
    private final PaymentRepository paymentRepository;
    private final WashQueueRepository washQueueRepository;
    private final QueueService queueService;
    private final WashBayService washBayService;
    private final AssignmentService assignmentService;
    private final LoyaltyService loyaltyService;
    private final PaymentService paymentService;



    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, Long customerId) {
        System.out.println(">>> Entered createBooking with customerId: " + customerId);
        try {
            // 1. Lấy customer
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

            // 2. Lấy vehicle
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + request.getVehicleId()));

            // 3. Lấy service package
            ServicePackage servicePackage = packageRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service package not found with id: " + request.getPackageId()));

            // 4. Kiểm tra slot
            if (!checkSlotAvailable(request.getScheduledTime(), request.getPackageId())) {
                throw new BookingConflictException("Time slot not available for package " + request.getPackageId() + " at " + request.getScheduledTime());
            }

            // 5. Tính giá (có bắt lỗi)
            Long finalPrice;

            try {
                finalPrice = pricingService.calculateFinalPrice(
                        request.getPackageId(),
                        request.getScheduledTime(),
                        customer.getMembershipTier() != null
                                ? customer.getMembershipTier().name()
                                : "MEMBER",
                        request.getVoucherCode()
                );
            } catch (Exception e) {
                throw new RuntimeException("Cannot calculate booking price", e);
            }

            // 6. Tạo QR code (có bắt lỗi)
            String qrCode;
            try {
                qrCode = qrCodeService.generateQRCodeForBooking(UUID.randomUUID().toString());
                System.out.println(">>> QRCodeService generated: " + qrCode);
            } catch (Exception e) {
                System.err.println(" ERROR in QRCodeService: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("QR code generation failed: " + e.getMessage(), e);
            }

            // 7. Xây dựng booking entity
            Booking booking = Booking.builder()
                    .customer(customer)
                    .vehicle(vehicle)
                    .servicePackage(servicePackage)
                    .scheduledTime(request.getScheduledTime())
                    .status(BookingStatus.PENDING)
                    .totalPrice(finalPrice != null ? finalPrice.longValue() : 0L)
                    .voucherCode(request.getVoucherCode())
                    .qrCode(qrCode)
                    .build();

            // 8. Lưu
            Booking saved = bookingRepository.save(booking);

            // sinh token QR
            String qrToken = UUID.randomUUID().toString();

            saved.setQrCode(qrToken);

            saved = bookingRepository.save(saved);

            // =========================
// TÍNH TIỀN ĐẶT CỌC 30%
// =========================
            Long deposit = Math.round(saved.getTotalPrice() * 0.3);

            saved.setDepositAmount(deposit);

            saved.setFinalAmount(saved.getTotalPrice());

            saved.setRemainingAmount(
                    saved.getTotalPrice() - deposit
            );

            saved.setDeposited(false);

            saved = bookingRepository.save(saved);

//            Payment payment = Payment.builder()
//
//                    .booking(saved)
//
//                    .amount(deposit)
//
//                    .paymentType(PaymentType.DEPOSIT)
//
//                    .status(PaymentStatus.PENDING)
//
//                    .method(PaymentMethod.PAYOS)
//
//                    .build();
//
//            paymentRepository.save(payment);
//
//            // sinh ảnh QR
//            byte[] qrImage =
//                    qrCodeService.generateQRCodeImage(qrToken);
//
//            // gửi email
//            emailService.sendBookingConfirmation(
//                    customer.getEmail(),
//                    customer.getFullName(),
//
//                    "Booking #" + saved.getId()
//                            + "<br>Ngày rửa: "
//                            + saved.getScheduledTime()
//                            + "<br>Dịch vụ: "
//                            + saved.getServicePackage().getName()
//                            + "<br>Số tiền: "
//                            + saved.getTotalPrice()
//                            + " VNĐ",
//
//                    Base64.getEncoder()
//                            .encodeToString(qrImage)
//            );
            // Tạo Payment + gửi email PayOS
            paymentService.createPaymentForBooking(saved);

            return mapToResponse(saved);


        } catch (Exception e) {
            System.err.println(" UNEXPECTED ERROR in createBooking: " + e.getMessage());
            e.printStackTrace();
            throw e; // Ném lại để controller hoặc global handler xử lý
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, Long customerId) {
        System.out.println(">>> cancelBooking called for bookingId: " + bookingId + ", customerId: " + customerId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Not authorized to cancel this booking");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel booking with status: " + booking.getStatus());
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        System.out.println(">>> Booking " + bookingId + " cancelled");
    }

    @Override
    @Transactional
    public BookingResponse rescheduleBooking(Long bookingId, LocalDateTime newTime, Long customerId) {
        System.out.println(">>> rescheduleBooking called for bookingId: " + bookingId + ", newTime: " + newTime);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Not authorized to reschedule this booking");
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
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        return bookingRepository.findByCustomer(customer).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse updateBookingStatus(Long bookingId, BookingStatus status, Long actorId) {
        System.out.println(">>> updateBookingStatus called for bookingId: " + bookingId + ", new status: " + status);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        booking.setStatus(status);
        Booking updated = bookingRepository.save(booking);
        return mapToResponse(updated);
    }

    @Override
    public boolean checkSlotAvailable(LocalDateTime scheduledTime, Long servicePackageId) {
        long count = bookingRepository.countActiveBookingsInPeriod(
                scheduledTime.minusMinutes(30),
                scheduledTime.plusMinutes(30)
        );
        // Tạm cho phép tối đa 100 booking (luôn true, để tránh lỗi)
        return count < 100;
    }

    @Override
    public List<LocalDateTime> getAvailableSlots(LocalDateTime date, Long servicePackageId) {
        // TODO: implement later
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

    @Override
    public List<BookingResponse> getActiveJobs() {
        return bookingRepository.findByStatusIn(List.of(
                        BookingStatus.WAITING, BookingStatus.WASHING, BookingStatus.DRYING,
                        BookingStatus.CHECKED_IN, BookingStatus.COMPLETED))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<BookingResponse> getReceptionBookings() {
        return bookingRepository.findByStatusIn(List.of(
                        BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN,
                        BookingStatus.WAITING, BookingStatus.WASHING, BookingStatus.DRYING,
                        BookingStatus.COMPLETED))
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse checkinBooking(Long bookingId) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        BookingStatus s = b.getStatus();
        if (s == BookingStatus.PENDING || s == BookingStatus.CONFIRMED || s == BookingStatus.CHECKED_IN) {
            b.setStatus(BookingStatus.WAITING);
            b.setCheckinTime(LocalDateTime.now());
        } else {
            throw new IllegalStateException("Đơn không thể check-in ở trạng thái " + s);
        }
        return mapToResponse(bookingRepository.save(b));
    }

    @Override
    @Transactional
    public BookingResponse assignBay(Long bookingId, Long bayId) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        WashBay bay = washBayRepository.findById(bayId)
                .orElseThrow(() -> new ResourceNotFoundException("Bay not found: " + bayId));
        b.setAssignedBay(bay);
        return mapToResponse(bookingRepository.save(b));
    }

    @Override
    @Transactional
    public CheckinInfoResponse checkIn(String qrCode) {

        Booking booking = bookingRepository.findByQrCode(qrCode)
                .orElseThrow(() ->
                        new RuntimeException("QR không tồn tại"));


        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking đã bị hủy");
        }


        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            throw new RuntimeException("Booking đã check-in");
        }


        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException("Booking đã hoàn thành");
        }



        // 1. Cập nhật trạng thái check-in

        booking.setStatus(BookingStatus.CHECKED_IN);

        booking.setCheckinTime(LocalDateTime.now());

        bookingRepository.save(booking);



        // 2. Đưa xe vào hàng đợi

        queueService.addToQueue(
                booking.getId()
        );



        // 3. Tự động gán Wash Bay

        autoAssignBay(
                booking.getId()
        );

        // 4. Tự động gán Washer
        assignmentService.autoAssignWasher(
                booking.getId()
        );



        return CheckinInfoResponse.builder()
                .bookingId(booking.getId())
                .customerName(booking.getCustomer().getFullName())
                .vehicle(booking.getVehicle().getLicensePlate())
                .servicePackage(booking.getServicePackage().getName())
                .status(BookingStatus.WAITING)
                .message("Check-in thành công, xe đã vào hàng đợi")
                .build();
    }
//    @Override
//    @Transactional
//    public BookingResponse advanceBookingStatus(Long bookingId) {
//        Booking b = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
//        BookingStatus s = b.getStatus();
//        if (s == BookingStatus.PENDING || s == BookingStatus.CONFIRMED
//                || s == BookingStatus.CHECKED_IN || s == BookingStatus.WAITING) {
//            b.setStatus(BookingStatus.WASHING);
//        } else if (s == BookingStatus.WASHING) {
//            b.setStatus(BookingStatus.DRYING);
//        } else if (s == BookingStatus.DRYING) {
//            b.setStatus(BookingStatus.COMPLETED);
//            b.setCompletedTime(LocalDateTime.now());
//        } else {
//            throw new IllegalStateException("Không thể chuyển tiếp từ trạng thái " + s);
//        }
//        return mapToResponse(bookingRepository.save(b));
//    }
@Override
@Transactional
public BookingResponse advanceBookingStatus(Long bookingId) {

    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Booking not found"));

    BookingStatus status = booking.getStatus();

    switch (status) {

        case WAITING -> {

            booking.setStatus(BookingStatus.WASHING);

            bookingRepository.save(booking);

            washQueueRepository.findByBookingId(bookingId)
                    .ifPresent(queue -> {

                        queue.setStatus(BookingStatus.WASHING);

                        queue.setStartedAt(LocalDateTime.now());

                        washQueueRepository.save(queue);
                    });
        }

//        case WASHING -> {
//
//            booking.setStatus(BookingStatus.DRYING);
//
//            bookingRepository.save(booking);
//        }

        case DRYING -> {

            booking.setStatus(BookingStatus.COMPLETED);
            loyaltyService.addPoints(
                    booking.getCustomer().getId(),
                    booking.getId(),
                    booking.getTotalPrice().longValue()
            );

            booking.setCompletedTime(LocalDateTime.now());

            washQueueRepository.findByBookingId(bookingId)
                    .ifPresent(queue -> {

                        queue.setFinishedAt(LocalDateTime.now());

                        washQueueRepository.save(queue);

                    });

            bookingRepository.save(booking);

            // xóa khỏi hàng đợi
            queueService.removeFromQueue(bookingId);

            // giải phóng bay
            if (booking.getAssignedBay() != null) {

                washBayService.releaseBay(
                        booking.getAssignedBay().getId()
                );
                queueService.moveToNextInQueue(
                        booking.getAssignedBay().getId()
                );
            }


        }

        default ->
                throw new IllegalStateException(
                        "Không thể chuyển trạng thái từ " + status);
    }

    return mapToResponse(booking);
}

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse resp = BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer() != null ? booking.getCustomer().getId() : null)
                .vehicleId(booking.getVehicle() != null ? booking.getVehicle().getId() : null)
                .packageId(booking.getServicePackage() != null ? booking.getServicePackage().getId() : null)
                .scheduledTime(booking.getScheduledTime())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .depositAmount(booking.getDepositAmount())
                .deposited(booking.getDeposited())
                .finalAmount(booking.getFinalAmount())
                .remainingAmount(booking.getRemainingAmount())
                .qrCode(booking.getQrCode())
                .packageName(booking.getServicePackage() != null ? booking.getServicePackage().getName() : null)
                .licensePlate(booking.getVehicle() != null ? booking.getVehicle().getLicensePlate() : null)
                .carBrand(booking.getVehicle() != null ? booking.getVehicle().getBrand() : null)
                .carModel(booking.getVehicle() != null ? booking.getVehicle().getModel() : null)
                .customerName(booking.getCustomer() != null ? booking.getCustomer().getFullName() : null)
                .bayNumber(booking.getAssignedBay() != null ? booking.getAssignedBay().getBayNumber() : null)
                .paid(paymentRepository.findFirstByBookingId(booking.getId())
                        .map(p -> p.getStatus() == PaymentStatus.PAID).orElse(false))
                .build();
        resp.setNote(booking.getNote());   // set note bằng setter (từ @Data)
        return resp;
    }
    @Override
    @Transactional
    public BookingResponse updateNote(Long bookingId, String note) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        b.setNote(note);
        return mapToResponse(bookingRepository.save(b));
    }
    @Transactional
    public BookingResponse autoAssignBay(Long bookingId){


        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found"));
        WashBay bay =
                washBayRepository
                        .findByStatus(com.carwash.carwashsystem.enums.WashBayStatus.AVAILABLE)
                        .stream()
                        .findFirst()
                        .orElse(null);



        // chưa có bay trống
        if(bay == null){

            booking.setStatus(BookingStatus.WAITING);

            return mapToResponse(
                    bookingRepository.save(booking)
            );
        }



        booking.setAssignedBay(bay);


        bay.setStatus(
                com.carwash.carwashsystem.enums.WashBayStatus.OCCUPIED
        );


        washBayRepository.save(bay);



        booking.setStatus(BookingStatus.WAITING);


        return mapToResponse(
                bookingRepository.save(booking)
        );

    }
    @Override
    @Transactional
    public BookingResponse startWash(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found"));

        // Chỉ được bắt đầu khi đang WAITING
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalStateException(
                    "Booking is not in WAITING status");
        }

        // Phải có Wash Bay
        if (booking.getAssignedBay() == null) {
            throw new IllegalStateException(
                    "Wash Bay has not been assigned");
        }

        // Phải có Washer
        if (booking.getAssignedWasher() == null) {
            throw new IllegalStateException(
                    "Washer has not been assigned");
        }

        // Cập nhật trạng thái
        booking.setStatus(BookingStatus.WASHING);

        // Lưu thời gian bắt đầu
        booking.setWashStartedTime(LocalDateTime.now());

        bookingRepository.save(booking);

        // Đồng bộ trạng thái Queue
        washQueueRepository.findByBookingId(bookingId)
                .ifPresent(queue -> {

                    queue.setStatus(BookingStatus.WASHING);

                    queue.setStartedAt(LocalDateTime.now());

                    washQueueRepository.save(queue);

                });

        return mapToResponse(booking);
    }
}