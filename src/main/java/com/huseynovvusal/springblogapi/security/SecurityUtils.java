package com.huseynovvusal.springblogapi.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for accessing security-related information from the current context.
 * Provides methods to retrieve the authenticated user's ID and username.
 */
@Slf4j
public final class SecurityUtils {

    // Prevent instantiation
    private SecurityUtils() {}

    /**
     * Retrieves the ID of the currently authenticated user.
     *
     * @return the user ID
     * @throws IllegalStateException if no authenticated principal is found or type is unexpected
     */
    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            log.warn("Attempted to access user ID but no authenticated principal found");
            throw new IllegalStateException("No authenticated principal");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserPrincipal up) {
            log.debug("Retrieved user ID: {}", up.id());
            return up.id();
        }

        log.error("Unexpected principal type: {}", principal.getClass().getName());
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
    }

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return the username
     * @throws IllegalStateException if no authenticated principal is found or type is unexpected
     */
    public static String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            log.warn("Attempted to access username but no authenticated principal found");
            throw new IllegalStateException("No authenticated principal");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserPrincipal up) {
            log.debug("Retrieved username from UserPrincipal: {}", up.username());
            return up.username();
        }

        if (principal instanceof String s) {
            log.debug("Retrieved username from String principal: {}", s);
            return s;
        }

        log.error("Unexpected principal type: {}", principal.getClass().getName());
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
    }
}
