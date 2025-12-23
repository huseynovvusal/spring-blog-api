package com.huseynovvusal.springblogapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Entity representing a registered user in the system.
 * Includes personal details, credentials, role, and authored blogs.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username used for login and identification.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * First name of the user.
     */
    @Column(nullable = false)
    private String firstName;

    /**
     * Last name of the user.
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * Unique email address of the user.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Hashed password for authentication.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Indicates whether the user account is blocked.
     */
    @Column(nullable = false)
    private boolean isBlocked = false;

    /**
     * Role assigned to the user (e.g., USER, ADMIN).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * List of blogs authored by the user.
     */
    @OneToMany(mappedBy = "author", orphanRemoval = true)
    private List<Blog> blogs;
}
