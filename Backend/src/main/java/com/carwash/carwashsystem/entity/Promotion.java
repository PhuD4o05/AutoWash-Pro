package com.carwash.carwashsystem.entity;

import com.carwash.carwashsystem.enums.MembershipTier;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


    @Entity
    @Table(name = "promotions")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Promotion {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String code;

        private String name;
        private String description;

        @Enumerated(EnumType.STRING)
        private MembershipTier applicableTier; // null = áp dụng mọi hạng

        private Integer discountPercent;        // giảm %
        private Long discountAmount;            // hoặc giảm trực tiếp VND

        private LocalDateTime startDate;
        private LocalDateTime endDate;

        @Builder.Default
        private Boolean isActive = true;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @OneToMany(mappedBy = "promotion")
        private List<Voucher> vouchers;

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

