package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import com.huseynovvusal.springblogapi.dto.BlockUserResponse;
import com.huseynovvusal.springblogapi.dto.BlockUserRequest;
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
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            log.error("[UserService] No authenticated user found.");
            throw new UsernameNotFoundException("No authenticated user found");
        }
        String username = authentication.getName();
        log.info("[UserService] Current username: {}", username);
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

    public BlockUserResponse changeBlockStatus(BlockUserRequest request) {
        User user = getUserByUsername(request.getUsername());
        user.setBlocked(request.getIsBlocked());
        userRepository.save(user);
        BlockUserResponse response = new BlockUserResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setIsBlocked(user.isBlocked());
        log.info("User {} has been {}", user.getUsername(), user.isBlocked() ? "blocked" : "unblocked");
        return response;
    }
}
