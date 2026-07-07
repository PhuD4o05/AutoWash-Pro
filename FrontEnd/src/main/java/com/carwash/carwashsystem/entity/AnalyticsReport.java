package com.carwash.carwashsystem.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reportDate;
    private String reportType;   // DAILY, WEEKLY, MONTHLY

    private Long totalBookings;
    private Long totalRevenue;
    private Long totalCustomers;
    private String topService;    // tên dịch vụ phổ biến nhất

    private String dataJson;      // lưu thêm dữ liệu chi tiết dạng JSON

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
