package com.carwash.carwashsystem.scheduler;

import com.carwash.carwashsystem.service.interfaces.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShiftAssignmentScheduler {
    private final AssignmentService assignmentService;

    // Chạy mỗi ngày lúc 6:00 AM để auto assign ca cho nhân viên
    @Scheduled(cron = "0 0 6 * * ?")
    public void autoAssignShifts() {
        LocalDate today = LocalDate.now();
        log.info("Auto-assigning shifts for date: {}", today);
        try {
            assignmentService.autoAssignWashersForShift(today.toString(), "MORNING");
            assignmentService.autoAssignWashersForShift(today.toString(), "AFTERNOON");
            assignmentService.autoAssignWashersForShift(today.toString(), "EVENING");
            log.info("Shift assignment completed successfully");
        } catch (Exception e) {
            log.error("Error auto-assigning shifts: {}", e.getMessage());
        }
    }
}