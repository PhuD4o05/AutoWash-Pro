package com.carwash.carwashsystem.config;

import com.carwash.carwashsystem.entity.Account;
import com.carwash.carwashsystem.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class PasswordResetRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner resetPasswords() {
        return args -> {

            reset("admin@test.vn");
            reset("recep@test.vn");
            reset("washer@test.vn");

            System.out.println("===== PASSWORD RESET SUCCESS =====");
        };
    }

    private void reset(String email) {
        accountRepository.findByEmail(email).ifPresent(account -> {
            account.setPassword(passwordEncoder.encode("123456"));
            accountRepository.save(account);
            System.out.println(email + " -> password = 123456");
        });
    }
}