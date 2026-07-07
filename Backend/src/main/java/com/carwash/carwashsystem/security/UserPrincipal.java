package com.carwash.carwashsystem.security;

import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.entity.Receptionist;
import com.carwash.carwashsystem.entity.Washer;
import com.carwash.carwashsystem.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Role role;
    private boolean isActive;

    public UserPrincipal(Long id, String username, String password, Role role, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }

    public static UserPrincipal create(Customer customer) {
        return new UserPrincipal(
                customer.getId(),
                customer.getPhoneNumber(),
                customer.getPassword(),
                customer.getRole(),
                customer.getIsActive()
        );
    }

    public static UserPrincipal create(Receptionist receptionist) {
        return new UserPrincipal(
                receptionist.getId(),
                receptionist.getPhoneNumber(),
                receptionist.getPassword(),
                receptionist.getRole(),
                receptionist.getIsActive()
        );
    }

    public static UserPrincipal create(Washer washer) {
        return new UserPrincipal(
                washer.getId(),
                washer.getPhoneNumber(),
                washer.getPassword(),
                washer.getRole(),
                washer.getIsActive()
        );
    }

    public Long getId() { return id; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return isActive; }
}