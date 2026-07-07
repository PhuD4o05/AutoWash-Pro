package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.WashBayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WashBayResponse {
    private Long id;
    private String bayNumber;
    private WashBayStatus status;
}
