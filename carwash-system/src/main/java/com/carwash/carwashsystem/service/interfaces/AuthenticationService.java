package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.GoogleLoginRequest;
import com.carwash.carwashsystem.dto.request.LoginRequest;
import com.carwash.carwashsystem.dto.request.RegisterRequest;
import com.carwash.carwashsystem.dto.response.LoginResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
    void register(RegisterRequest request);
    void createQuickCustomerByPhone(String phoneNumber);
    LoginResponse loginWithGoogle(GoogleLoginRequest request);
    void changePassword(String username, String oldPassword, String newPassword);
}