package com.carwash.carwashsystem.security;

//import com.carwash.carwashsystem.entity.Customer;
//import com.carwash.carwashsystem.entity.Receptionist;
//import com.carwash.carwashsystem.entity.Washer;
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

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return customerRepository.findByPhoneNumber(username)
//                .map(UserPrincipal::create)
//                .or(() -> receptionistRepository.findByPhoneNumber(username).map(UserPrincipal::create))
//                .or(() -> washerRepository.findByPhoneNumber(username).map(UserPrincipal::create))
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//    }
    public UserDetails loadUserById(Long id) {
        return customerRepository.findById(id)
            .map(UserPrincipal::create)
            .or(() -> receptionistRepository.findById(id)
                    .map(UserPrincipal::create))
            .or(() -> washerRepository.findById(id)
                    .map(UserPrincipal::create))
            .orElseThrow(() ->
                    new UsernameNotFoundException("User not found"));
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) {
          return customerRepository.findByEmail(username)
                .map(UserPrincipal::create)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
      }
}