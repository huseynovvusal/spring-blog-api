package com.huseynovvusal.springblogapi.model;

/**
 * Enum representing user roles within the application.
 * Used for authorization and access control.
 */
public enum Role {
    /**
     * Standard user with limited access.
     */
    USER,

    /**
     * Administrator with elevated privileges.
     */
    ADMIN
}
