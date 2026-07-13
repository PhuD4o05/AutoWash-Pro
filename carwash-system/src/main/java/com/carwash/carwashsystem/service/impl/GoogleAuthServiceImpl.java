package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.enums.AuthProvider;
import com.carwash.carwashsystem.enums.MembershipTier;
import com.carwash.carwashsystem.enums.Role;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.service.interfaces.GoogleAuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final CustomerRepository customerRepository;

    @Value("${google.client-id}")
    private String googleClientId;

    @Override
    public Customer authenticate(String idToken) {

        try {

            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(
                            new NetHttpTransport(),
                            GsonFactory.getDefaultInstance())
                            .setAudience(Collections.singletonList(googleClientId))
                            .build();

//            GoogleIdToken googleIdToken =
//                    verifier.verify(idToken);
//
//            if (googleIdToken == null) {
//                throw new RuntimeException("Invalid Google Token");
//            }
            System.out.println("Google Client ID = " + googleClientId);

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            System.out.println("Verify result = " + googleIdToken);

            if (googleIdToken == null) {
                throw new RuntimeException("Invalid Google Token");
            }

            GoogleIdToken.Payload payload =
                    googleIdToken.getPayload();

            String email = payload.getEmail();

            String fullName =
                    (String) payload.get("name");

            String picture =
                    (String) payload.get("picture");

            return customerRepository.findByEmail(email)
                    .orElseGet(() -> {

                        Customer customer =
                                Customer.builder()
                                        .email(email)
                                        .fullName(fullName)
                                        .avatarUrl(picture)
                                        .role(Role.CUSTOMER)
                                        .membershipTier(MembershipTier.MEMBER)
                                        .totalPoints(0)
                                        .currentPoints(0)
                                        .isActive(true)
                                        .authProvider(AuthProvider.GOOGLE)
                                        .build();

                        return customerRepository.save(customer);

                    });

//        } catch (Exception ex) {
//            throw new RuntimeException("Google Authentication Failed", ex);
//        }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Google Authentication Failed: " + ex.getMessage(), ex);
        }

    }
}