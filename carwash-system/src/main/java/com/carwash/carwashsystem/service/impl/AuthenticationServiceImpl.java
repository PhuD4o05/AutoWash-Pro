package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.GoogleLoginRequest;
import com.carwash.carwashsystem.dto.request.LoginRequest;
import com.carwash.carwashsystem.dto.request.RegisterRequest;
import com.carwash.carwashsystem.dto.response.LoginResponse;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.entity.RefreshToken;
import com.carwash.carwashsystem.enums.AuthProvider;
import com.carwash.carwashsystem.enums.MembershipTier;
import com.carwash.carwashsystem.enums.Role;
import com.carwash.carwashsystem.exception.DuplicateResourceException;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.repository.RefreshTokenRepository;
import com.carwash.carwashsystem.security.JwtTokenProvider;
import com.carwash.carwashsystem.security.UserPrincipal;
import com.carwash.carwashsystem.service.interfaces.AuthenticationService;
import com.carwash.carwashsystem.service.interfaces.GoogleAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final CustomerRepository customerRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final GoogleAuthService googleAuthService;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (customerRepository.existsByPhoneNumber(request.getPhone())) {
            throw new DuplicateResourceException("Phone already exists");
        }

        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .membershipTier(MembershipTier.MEMBER)
                .totalPoints(0)
                .isActive(true)
                .authProvider(AuthProvider.LOCAL)
                .build();
        customerRepository.save(customer);
    }

    @Override
    public void createQuickCustomerByPhone(String phoneNumber) {
        if (!customerRepository.existsByPhoneNumber(phoneNumber)) {
            Customer customer = Customer.builder()
                    .phoneNumber(phoneNumber)
                    .fullName("Guest")
                    .isActive(true)
                    .role(Role.CUSTOMER)
                    .membershipTier(MembershipTier.MEMBER)
                    .totalPoints(0)
                    .build();
            customerRepository.save(customer);
        }
    }

    @Override
    public LoginResponse loginWithGoogle(GoogleLoginRequest request) {

        Customer customer = googleAuthService.authenticate(request.getIdToken());

        UserPrincipal principal = UserPrincipal.create(customer);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        String accessToken = tokenProvider.generateAccessToken(authentication);

        String refreshTokenStr = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .username(customer.getEmail())
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .fullName(customer.getFullName())
                .role(customer.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Customer customer;

        if (username.contains("@")) {
            customer = customerRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            customer = customerRepository.findByPhoneNumber(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        // Kiểm tra xem user có đăng nhập bằng Google không (không có mật khẩu)
        if (customer.getAuthProvider() == AuthProvider.GOOGLE) {
            throw new RuntimeException("Tài khoản Google không thể đổi mật khẩu. Vui lòng đăng nhập bằng Google.");
        }

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }

        // Mã hóa mật khẩu mới và lưu
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }


    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshTokenStr = UUID.randomUUID().toString();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String username = principal.getUsername();
        String fullName = principal.getFullName();
        String role = principal.getRole().name();

        RefreshToken tokenEntity = RefreshToken.builder()
                .token(refreshTokenStr)
                .username(username)
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                .revoked(false)
                .build();
        refreshTokenRepository.save(tokenEntity);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .fullName(fullName)
                .role(role)
                .build();
    }

    @Override
    public LoginResponse refreshToken(String refreshTokenStr) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }
        // Tạo access token mới dựa trên username (phone)
        String newAccessToken = tokenProvider.generateAccessTokenByUsername(token.getUsername());
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenStr)
                .build();
    }

    @Override
    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });

    }


}