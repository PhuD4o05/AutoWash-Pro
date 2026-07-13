package com.carwash.carwashsystem.security;

import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.entity.Receptionist;
import com.carwash.carwashsystem.entity.Washer;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.repository.ReceptionistRepository;
import com.carwash.carwashsystem.repository.WasherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final ReceptionistRepository receptionistRepository;
    private final WasherRepository washerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("Username cannot be null");
        }

        // ============================================================
        // 1. KIỂM TRA NẾU USERNAME LÀ EMAIL (có chứa @)
        // ============================================================
//        if (username.contains("@")) {
//            return customerRepository.findByEmail(username)
//                    .map(UserPrincipal::create)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
//        }
        if (username.contains("@")) {

            return customerRepository.findByEmail(username)
                    .map(UserPrincipal::create)
                    .or(() -> receptionistRepository.findByEmail(username)
                            .map(UserPrincipal::create))
                    .or(() -> washerRepository.findByEmail(username)
                            .map(UserPrincipal::create))
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found: " + username
                            ));
        }

        // ============================================================
        // 2. NẾU USERNAME LÀ SỐ ĐIỆN THOẠI (hoặc ID)
        // ============================================================
        // Kiểm tra xem có phải số điện thoại không (độ dài 10-11 số)
//        if (username.matches("\\d{10,11}")) {
//            // Tìm theo số điện thoại trong bảng customers
//            return customerRepository.findByPhoneNumber(username)
//                    .map(UserPrincipal::create)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + username));
//        }
        if (username.matches("\\d{10,11}")) {

            return customerRepository.findByPhoneNumber(username)
                    .map(UserPrincipal::create)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found with phone: " + username
                            ));
        }

        // ============================================================
        // 3. NẾU USERNAME LÀ ID (số)
        // ============================================================
//        if (username.matches("\\d+")) {
//            Long id = Long.parseLong(username);
//            return customerRepository.findById(id)
//                    .map(UserPrincipal::create)
//                    .or(() -> receptionistRepository.findById(id)
//                            .map(UserPrincipal::create))
//                    .or(() -> washerRepository.findById(id)
//                            .map(UserPrincipal::create))
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
//        }
        if (username.matches("\\d+")) {

            Long id = Long.parseLong(username);

            return customerRepository.findById(id)
                    .map(UserPrincipal::create)
                    .or(() -> receptionistRepository.findById(id)
                            .map(UserPrincipal::create))
                    .or(() -> washerRepository.findById(id)
                            .map(UserPrincipal::create))
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found with id: " + id
                            ));
        }

        // ============================================================
        // 4. KHÔNG TÌM THẤY → NÉN EXCEPTION
        // ============================================================
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    public UserDetails loadUserById(Long id) {

        return customerRepository.findById(id)
                .map(UserPrincipal::create)
                .or(() -> receptionistRepository.findById(id)
                        .map(UserPrincipal::create))
                .or(() -> washerRepository.findById(id)
                        .map(UserPrincipal::create))
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with id: " + id
                        ));
    }
}