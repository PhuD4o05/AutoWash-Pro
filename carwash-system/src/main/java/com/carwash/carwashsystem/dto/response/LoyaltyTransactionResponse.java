package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.LoyaltyTransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class LoyaltyTransactionResponse {

    private Long id;

    private Integer points;

    private LoyaltyTransactionType type;

    private String description;

    private LocalDateTime createdAt;

}