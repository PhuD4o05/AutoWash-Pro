package com.carwash.carwashsystem.dto.request;

import com.carwash.carwashsystem.enums.ShiftType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAssignmentRequest {
    @NotNull
    private Long washerId;
    @NotNull
    private LocalDate shiftDate;
    @NotNull
    private ShiftType shiftType; // MORNING, AFTERNOON, EVENING

    private Long workShiftId;
    private Long washBayId;
}