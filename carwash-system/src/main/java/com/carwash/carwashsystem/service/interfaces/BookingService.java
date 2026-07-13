package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.BookingRequest;
import com.carwash.carwashsystem.dto.response.BookingResponse;
import com.carwash.carwashsystem.dto.response.CheckinInfoResponse;
import com.carwash.carwashsystem.enums.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, Long customerId);
    void cancelBooking(Long bookingId, Long customerId);   // void
    BookingResponse rescheduleBooking(Long bookingId, LocalDateTime newTime, Long customerId);
    BookingResponse getBookingById(Long bookingId);
    List<BookingResponse> getBookingsByCustomer(Long customerId);
    BookingResponse updateBookingStatus(Long bookingId, BookingStatus status, Long actorId);
    boolean checkSlotAvailable(LocalDateTime scheduledTime, Long servicePackageId);
    List<LocalDateTime> getAvailableSlots(LocalDateTime date, Long servicePackageId);
    List<BookingResponse> getTodayBookings();
    List<BookingResponse> getActiveJobs();
    BookingResponse advanceBookingStatus(Long bookingId);
    BookingResponse updateNote(Long bookingId, String note);
    List<BookingResponse> getReceptionBookings();
    BookingResponse checkinBooking(Long bookingId);
    BookingResponse assignBay(Long bookingId, Long bayId);// thêm
    CheckinInfoResponse checkIn(String qrCode);
    BookingResponse autoAssignBay(Long bookingId);
    BookingResponse startWash(Long bookingId);
}