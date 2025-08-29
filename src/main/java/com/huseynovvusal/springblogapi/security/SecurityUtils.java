package com.huseynovvusal.springblogapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated principal");
        }
        Object principal = auth.getPrincipal();

        if (principal instanceof UserPrincipal up) {
            return up.id();              // <-- record accessor
        }
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
    }

    public static String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated principal");
        }
        Object principal = auth.getPrincipal();

        if (principal instanceof UserPrincipal up) {
            return up.username();        // <-- record accessor
        }
        // Fallback: sometimes Spring sets principal to a String (username)
        if (principal instanceof String s) {
            return s;
        }
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
    }
}
