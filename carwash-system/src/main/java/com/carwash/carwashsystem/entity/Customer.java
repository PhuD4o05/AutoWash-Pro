package com.carwash.carwashsystem.entity;

import com.carwash.carwashsystem.enums.MembershipTier;
import com.carwash.carwashsystem.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import com.carwash.carwashsystem.enums.AuthProvider;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phoneNumber;
    private String email;
    private String password;
    private String avatarUrl;


    //@Enumerated(EnumType.STRING)
   // private MembershipTier membershipTier;

//    private Integer totalPoints;
//    private Integer currentPoints;
@Builder.Default
private Integer totalPoints = 0;


    @Builder.Default
    private Integer currentPoints = 0;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    private MembershipTier membershipTier = MembershipTier.MEMBER;
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<LoyaltyTransaction> loyaltyTransactions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @OneToOne
    @JoinColumn(name="account_id")
    private Account account;



}