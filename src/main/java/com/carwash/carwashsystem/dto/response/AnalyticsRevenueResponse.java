package com.carwash.carwashsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRevenueResponse {
    private Long totalRevenue;
    private Long totalBookings;
    private LocalDate date;
    private LocalDate startDate;
    private LocalDate endDate;
}