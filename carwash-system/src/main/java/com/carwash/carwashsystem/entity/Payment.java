package com.carwash.carwashsystem.entity;

import com.carwash.carwashsystem.enums.PaymentMethod;
import com.carwash.carwashsystem.enums.PaymentStatus;
import com.carwash.carwashsystem.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Booking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    // Số tiền của lần thanh toán này
    private Long amount;

    // Đã đặt cọc bao nhiêu
    private Long depositAmount;

    // Còn phải trả bao nhiêu
    private Long remainingAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    // PayOS
    private String transactionId;

    private String checkoutUrl;

    private String qrCodeUrl;

    private LocalDateTime paidAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}