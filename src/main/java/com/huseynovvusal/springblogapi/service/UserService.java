package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for accessing and managing user-related operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the current User entity
     * @throws UsernameNotFoundException if the user is not found
     */
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("[UserService] Current username: {}", username);
        return getUserByUsername(username);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return the User entity
     * @throws UsernameNotFoundException if the user is not found
     */
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}
