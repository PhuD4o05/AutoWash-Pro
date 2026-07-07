package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        }
        throw new RuntimeException("User not authenticated");
    }
}
