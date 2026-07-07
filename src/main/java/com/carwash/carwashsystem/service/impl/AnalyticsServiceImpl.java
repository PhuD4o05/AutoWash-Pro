package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.response.AnalyticsRevenueResponse;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.PaymentRepository;
import com.carwash.carwashsystem.service.interfaces.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public AnalyticsRevenueResponse getRevenueByDate(LocalDate date) {
        Long revenue = paymentRepository.sumAmountByDate(date);
        Long bookings = bookingRepository.countByDate(date);
        return AnalyticsRevenueResponse.builder()
                .date(date)
                .totalRevenue(revenue)
                .totalBookings(bookings)
                .build();
    }

    @Override
    public AnalyticsRevenueResponse getRevenueByWeek(LocalDate startDate) {
        return getRevenueByDateRange(startDate, startDate.plusDays(6));
    }

    @Override
    public AnalyticsRevenueResponse getRevenueByMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return getRevenueByDateRange(startDate, endDate);
    }

    @Override
    public AnalyticsRevenueResponse getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        Long revenue = paymentRepository.sumAmountByDateRange(startDate, endDate);
        Long bookings = bookingRepository.countByDateRange(startDate, endDate);
        return AnalyticsRevenueResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(revenue)
                .totalBookings(bookings)
                .build();
    }

    @Override
    public Object getTopServices(LocalDate start, LocalDate end, int limit) {
        // TODO
        return null;
    }

    @Override
    public Object getPeakHours(LocalDate date) {
        // TODO
        return null;
    }
}