package com.carwash.carwashsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateRequest {
    private String fullName;
    @Pattern(regexp = "\\d{10,11}", message = "Số điện thoại không hợp lệ")
    private String phone;
    @Email
    private String email;
    private String password; // optional, nếu có thì cập nhật
}