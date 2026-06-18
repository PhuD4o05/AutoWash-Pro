package com.carwash.carwashsystem.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

    @Entity
    @Table(name = "vouchers")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Voucher {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String code;
        private String description;

        private Integer discountPercent;
        private Long discountAmount;

        private Integer requiredPoints;   // điểm cần để đổi (nếu đổi từ loyalty)
        private LocalDateTime validFrom;
        private LocalDateTime validUntil;

        @Builder.Default
        private Boolean isActive = true;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "promotion_id")
        private Promotion promotion;      // voucher có thể thuộc chương trình KM

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "customer_id")
        private Customer customer;        // nếu voucher là cá nhân (đổi từ điểm)

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
        }
    }


