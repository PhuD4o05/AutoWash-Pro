package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.WalkinBookingRequest;
import com.carwash.carwashsystem.dto.response.CheckinInfoResponse;
import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.entity.WashQueue;
import java.util.List;

public interface ReceptionistService {
    CheckinInfoResponse scanQRCheckin(String qrCode);
    CheckinInfoResponse manualCheckinByPhone(String phone);
    CheckinInfoResponse createWalkinBooking(WalkinBookingRequest request);
    List<Booking> getTodayBookings();
    List<WashQueue> getCurrentQueue();
    void confirmPayment(Long bookingId, String paymentMethod);
}