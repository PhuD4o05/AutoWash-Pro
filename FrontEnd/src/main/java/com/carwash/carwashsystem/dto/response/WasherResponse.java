package com.carwash.carwashsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasherResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Boolean isActive;
}
