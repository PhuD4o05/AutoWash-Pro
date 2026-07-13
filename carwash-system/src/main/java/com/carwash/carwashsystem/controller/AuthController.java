package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.request.GoogleLoginRequest;
import com.carwash.carwashsystem.dto.request.LoginRequest;
import com.carwash.carwashsystem.dto.request.RegisterRequest;
import com.carwash.carwashsystem.dto.response.LoginResponse;
import com.carwash.carwashsystem.service.interfaces.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Đăng ký, đăng nhập, refresh token")
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản khách hàng")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới access token")
    public ResponseEntity<LoginResponse> refresh(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginGoogle(
            @RequestBody GoogleLoginRequest request){

        return ResponseEntity.ok(
                authService.loginWithGoogle(request)
        );
    }
}
