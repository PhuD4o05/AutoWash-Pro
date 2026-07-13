package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.MembershipTier;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private MembershipTier membershipTier;
    private Integer totalPoints;
    private Integer currentPoints;
    private Boolean isActive;
    private com.carwash.carwashsystem.enums.Role role;
}