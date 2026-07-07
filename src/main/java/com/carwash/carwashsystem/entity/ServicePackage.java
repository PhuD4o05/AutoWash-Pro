package com.carwash.carwashsystem.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


    @Entity
    @Table(name = "service_packages")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ServicePackage {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String name;            // Express Wash, Basic Wash, ...

        private String description;
        private Integer estimatedMinutes; // thời gian dự kiến (phút)
        private Long basePrice;           // giá gốc (VND)

        @Builder.Default
        private Boolean isActive = true;
        private String vehicleType;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @OneToMany(mappedBy = "servicePackage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<Booking> bookings;

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

