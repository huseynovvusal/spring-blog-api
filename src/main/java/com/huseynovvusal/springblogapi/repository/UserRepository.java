package com.huseynovvusal.springblogapi.repository;

import com.huseynovvusal.springblogapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for accessing {@link User} entities.
 * Provides methods for querying users by username and email.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to search for
     * @return the user entity, or null if not found
     */
    User findByUsername(String username);

    /**
     * Finds a user by their unique email address.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
}
