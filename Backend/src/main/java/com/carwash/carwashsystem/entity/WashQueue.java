package com.carwash.carwashsystem.entity;

import com.carwash.carwashsystem.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wash_queue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WashQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;
    //private String status;

    private Integer queuePosition;   // thứ tự trong hàng chờ
    private LocalDateTime enqueuedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.WAITING; // WAITING / WASHING

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}
