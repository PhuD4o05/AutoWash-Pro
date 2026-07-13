package com.carwash.carwashsystem.entity;

import com.carwash.carwashsystem.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_package_id")
    private ServicePackage servicePackage;

    private LocalDateTime scheduledTime;
    private LocalDateTime checkinTime;
    private LocalDateTime washStartedTime;
    private LocalDateTime completedTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String qrCode;
    private Double totalPrice;

    /**
     * Tiền khách đặt cọc online
     */
    @Builder.Default
    private Long depositAmount = 0L;

    /**
     * Tổng tiền cuối cùng (gồm dịch vụ phát sinh)
     */
    private Long finalAmount;

    /**
     * Số tiền còn phải thanh toán
     */
    @Builder.Default
    private Long remainingAmount = 0L;
    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BookingExtraService> extraServices;
    private String voucherCode;
    private String note;

//    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
//    private Payment payment;
@OneToMany(
        mappedBy = "booking",
        cascade = CascadeType.ALL,
        orphanRemoval = true
)
private List<Payment> payments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_washer_id")
    private Washer assignedWasher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_bay_id")
    private WashBay assignedBay;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (depositAmount == null) {
            depositAmount = 0L;
        }

        if (finalAmount == null && totalPrice != null) {
            finalAmount = totalPrice.longValue();
        }

        if (remainingAmount == null) {
            remainingAmount = finalAmount - depositAmount;
        }
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        if (finalAmount != null) {
            remainingAmount = finalAmount - depositAmount;

            if (remainingAmount < 0) {
                remainingAmount = 0L;
            }
        }
    }

    @Builder.Default
    private Boolean paid = false;
//    private Boolean paid;

    @Builder.Default
    private Boolean deposited = false;


}