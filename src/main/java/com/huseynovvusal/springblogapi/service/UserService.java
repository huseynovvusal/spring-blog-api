package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.BlockUserRequest;
import com.huseynovvusal.springblogapi.dto.BlockUserResponse;
import com.huseynovvusal.springblogapi.dto.response.UserResponseDto;
import com.huseynovvusal.springblogapi.mapper.UserMapper;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Service for accessing and managing user-related operations. */
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
    if (authentication == null
        || !authentication.isAuthenticated()
        || "anonymousUser".equals(authentication.getName())) {
      log.error("[UserService] No authenticated user found.");
      throw new UsernameNotFoundException("No authenticated user found");
    }
    String username = authentication.getName();
    log.info("[UserService] Current username: {}", username);
    return getUserByUsername(username);
  }

  /**
   * Retrieves the currently authenticated user's profile as a DTO. This method handles both
   * authentication check and DTO conversion, keeping business logic and data transformation in the
   * service layer.
   *
   * @return the current user's profile information as UserResponseDto
   * @throws UsernameNotFoundException if the user is not authenticated
   */
  public UserResponseDto getCurrentUserProfile() {
    log.info("[UserService] Retrieving current user profile");
    User user = getCurrentUser();
    UserResponseDto response = UserMapper.toResponseDto(user);
    log.debug("[UserService] User profile retrieved for user: {}", user.getUsername());
    return response;
  }

  /**
   * Retrieves a user by their username.
   *
   * @param username the username to search for
   * @return the User entity
   * @throws UsernameNotFoundException if the user is not found
   */
  @CircuitBreaker(name = "default")
  public User getUserByUsername(String username) {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      log.warn("User not found with username: {}", username);
      throw new UsernameNotFoundException(String.format("User not found: %s", username));
    }
    return user;
  }

  public BlockUserResponse changeBlockStatus(BlockUserRequest request) {

    User user = getUserByUsername(request.getUsername());
    user.setBlocked(request.getIsBlocked());
    userRepository.save(user);

    log.info("User {} has been {}", user.getUsername(), user.isBlocked() ? "blocked" : "unblocked");

    return new BlockUserResponse(
        user.getFirstName(),
        user.getLastName(),
        user.getUsername(),
        user.getEmail(),
        user.isBlocked());
  }
}
