package com.carwash.carwashsystem.util;

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

    public static String getCurrentUserRole() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "GUEST";
        }
        return auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }
}