package com.huseynovvusal.springblogapi.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Lightweight principal stored in the Spring Security context.
 * Represents an authenticated user with minimal identity and role information.
 */
@Slf4j
public record UserPrincipal(
        Long id,
        String username,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    /**
     * Returns the authorities granted to the user.
     * Typically used for role-based access control.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.debug("Fetching authorities for user: {}", username);
        return authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     * Always returns null because password is not stored in the principal after login.
     */
    @Override
    public String getPassword() {
        log.debug("Password access requested for user: {}", username);
        return null;
    }

    /**
     * Returns the username used to authenticate the user.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user's account has expired.
     * Always returns true in this stateless context.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * Always returns true in this stateless context.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials have expired.
     * Always returns true in this stateless context.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * Always returns true in this stateless context.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
