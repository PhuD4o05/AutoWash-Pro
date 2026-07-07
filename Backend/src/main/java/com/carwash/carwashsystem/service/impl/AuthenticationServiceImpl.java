package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.LoginRequest;
import com.carwash.carwashsystem.dto.request.RegisterRequest;
import com.carwash.carwashsystem.dto.response.LoginResponse;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.entity.RefreshToken;
import com.carwash.carwashsystem.enums.MembershipTier;
import com.carwash.carwashsystem.enums.Role;
import com.carwash.carwashsystem.exception.DuplicateResourceException;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.repository.RefreshTokenRepository;
import com.carwash.carwashsystem.security.JwtTokenProvider;
import com.carwash.carwashsystem.security.UserPrincipal;
import com.carwash.carwashsystem.service.interfaces.AuthenticationService;
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
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshTokenStr = UUID.randomUUID().toString();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String username = principal.getUsername(); // lấy username (phoneNumber)

        RefreshToken tokenEntity = RefreshToken.builder()
                .token(refreshTokenStr)
                .username(username)                    //  sửa: dùng username
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)) // 7 days
                .revoked(false)
                .build();
        refreshTokenRepository.save(tokenEntity);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
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