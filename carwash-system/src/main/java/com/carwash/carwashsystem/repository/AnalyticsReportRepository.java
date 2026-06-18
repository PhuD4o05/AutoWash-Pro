package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.AnalyticsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsReportRepository extends JpaRepository<AnalyticsReport, Long> {
    List<AnalyticsReport> findByReportType(String reportType);
    //Optional<AnalyticsReport> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);
    Optional<AnalyticsReport> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT a FROM AnalyticsReport a WHERE a.createdAt >= :start AND a.createdAt <= :end ORDER BY a.createdAt DESC")
    List<AnalyticsReport> findReportsInDateRange(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);
}
