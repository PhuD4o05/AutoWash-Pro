package com.carwash.carwashsystem.dto.response;

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
public class WorkShiftResponse {
    private Long id;
    private LocalDate date;
    private ShiftType shiftType;
    private LocalTime startTime;
    private LocalTime endTime;
}
