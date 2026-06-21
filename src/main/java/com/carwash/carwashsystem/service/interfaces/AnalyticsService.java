package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.response.AnalyticsRevenueResponse;
import java.time.LocalDate;

public interface AnalyticsService {
    AnalyticsRevenueResponse getRevenueByDate(LocalDate date);
    AnalyticsRevenueResponse getRevenueByWeek(LocalDate startDate);
    AnalyticsRevenueResponse getRevenueByMonth(int year, int month);
    Object getTopServices(LocalDate start, LocalDate end, int limit);
    Object getPeakHours(LocalDate date);
    AnalyticsRevenueResponse getRevenueByDateRange(LocalDate startDate, LocalDate endDate);
}