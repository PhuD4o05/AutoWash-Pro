package com.carwash.carwashsystem.entity;

import com.carwash.carwashsystem.enums.WashBayStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

    @Entity
    @Table(name = "wash_bays")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class WashBay {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String bayNumber;   // "Bay 1", "Bay 2"

        @Enumerated(EnumType.STRING)
        @Builder.Default
        private WashBayStatus status = WashBayStatus.AVAILABLE;

        @OneToMany(mappedBy = "assignedBay")
        private List<Booking> bookings;

        @OneToMany(mappedBy = "washBay")
        private List<Assignment> assignments;   // phân công nhân viên theo ca
    }

