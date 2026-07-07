package com.carwash.carwashsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "washer_id")
    private Washer washer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_shift_id")
    private WorkShift workShift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wash_bay_id")
    private WashBay washBay;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

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