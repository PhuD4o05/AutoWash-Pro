package com.carwash.carwashsystem.dto.response;


import lombok.Data;
import java.time.LocalDateTime;


@Data
public class UserManagementResponse {


    private Long id;


    private String fullName;


    private String email;


    private String phone;


    private String role;


    private String status;


    private Integer loyaltyPoint;


    private LocalDateTime createdAt;

}