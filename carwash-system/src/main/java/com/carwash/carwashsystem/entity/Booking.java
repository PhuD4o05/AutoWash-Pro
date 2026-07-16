package com.carwash.carwashsystem.entity;


import com.carwash.carwashsystem.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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



    // ================= CUSTOMER =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customer_id")
    private Customer customer;



    // ================= VEHICLE =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="vehicle_id")
    private Vehicle vehicle;



    // ================= SERVICE =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="service_package_id")
    private ServicePackage servicePackage;



    // ================= TIME =================

    private LocalDateTime scheduledTime;

    private LocalDateTime checkinTime;

    private LocalDateTime washStartedTime;

    private LocalDateTime completedTime;



    // ================= STATUS =================

    @Enumerated(EnumType.STRING)
    private BookingStatus status;



    // ================= QR =================

    private String qrCode;



    // ================= MONEY =================


    /**
     * Giá gói rửa ban đầu
     */
    private Long totalPrice;



    /**
     * Tổng tiền cuối cùng
     * = totalPrice + extraServices
     */
    private Long finalAmount;



    /**
     * Tiền khách đặt cọc
     */
    @Builder.Default
    private Long depositAmount = 0L;



    /**
     * Tiền còn phải trả
     */
    @Builder.Default
    private Long remainingAmount = 0L;



    // ================= EXTRA SERVICE =================


    @Builder.Default
    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BookingExtraService> extraServices =
            new ArrayList<>();



    // ================= PAYMENT =================


    @Builder.Default
    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Payment> payments =
            new ArrayList<>();



    // ================= OTHER =================


    private String voucherCode;

    private String note;



    // ================= ASSIGN =================


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assigned_washer_id")
    private Washer assignedWasher;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assigned_bay_id")
    private WashBay assignedBay;



    // ================= FLAG =================


    @Builder.Default
    private Boolean paid = false;


    @Builder.Default
    private Boolean deposited = false;



    // ================= AUDIT =================


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



    @PrePersist
    public void create(){

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();


        if(totalPrice == null){
            totalPrice = 0L;
        }


        if(finalAmount == null){

            finalAmount = totalPrice;

        }


        if(depositAmount == null){

            depositAmount = 0L;

        }


        remainingAmount =
                finalAmount - depositAmount;


        if(remainingAmount < 0){

            remainingAmount = 0L;

        }

    }



    @PreUpdate
    public void update(){

        updatedAt = LocalDateTime.now();


        if(finalAmount != null){

            remainingAmount =
                    finalAmount - depositAmount;


            if(remainingAmount < 0){

                remainingAmount = 0L;

            }

        }

    }

}