package com.carwash.carwashsystem.entity;

import com.carwash.carwashsystem.enums.LoyaltyTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


    @Entity
    @Table(name = "loyalty_transactions")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class LoyaltyTransaction {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "customer_id", nullable = false)
        private Customer customer;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "booking_id")
        private Booking booking;          // null nếu là đổi thưởng

        @Enumerated(EnumType.STRING)
        private LoyaltyTransactionType type;

        private Integer points;           // số điểm thay đổi (có thể âm nếu type = REDEEMED)
        private String description;       // "Cộng từ booking #123" hoặc "Đổi voucher X"

        private LocalDateTime createdAt;

        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
        }
    }

