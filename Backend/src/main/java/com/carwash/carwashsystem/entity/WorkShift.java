package com.carwash.carwashsystem.entity;

import com.carwash.carwashsystem.enums.ShiftType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "work_shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "washer_id", nullable = false)
    private Washer washer;

    private LocalDate shiftDate;

    @Enumerated(EnumType.STRING)
    private ShiftType shiftType;

    private LocalTime startTime;
    private LocalTime endTime;

    @OneToMany(mappedBy = "workShift")
    private List<Assignment> assignments;

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