package com.carwash.carwashsystem.dto.request;

import com.carwash.carwashsystem.enums.ShiftType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkShiftRequest {
    private LocalDate date;
    private ShiftType shiftType;   // MORNING, AFTERNOON, EVENING
    private LocalTime startTime;
    private LocalTime endTime;
}
