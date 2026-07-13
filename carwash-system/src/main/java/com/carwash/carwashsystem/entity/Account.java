package com.carwash.carwashsystem.entity;

import com.carwash.carwashsystem.enums.Role;
import com.carwash.carwashsystem.enums.Status;   // your custom Status enum
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;   // now using your custom enum

    @OneToOne(mappedBy = "account")
    private Customer customer;

    private LocalDateTime createdAt;
}