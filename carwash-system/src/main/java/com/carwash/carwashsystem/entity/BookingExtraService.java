package com.carwash.carwashsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking_extra_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingExtraService {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;


    private String serviceName;


    private Long price;
}