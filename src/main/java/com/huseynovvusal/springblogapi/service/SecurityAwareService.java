package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.security.SecurityUtils;

/**
 * Interface for services that need to be aware of the current user's security context.
 * Provides a default method to retrieve the current user's ID, centralizing the logic.
 */
public interface SecurityAwareService {

    /**
     * Retrieves the ID of the currently authenticated user.
     *
     * @return the user ID as a Long
     */
    default Long currentUserId() {
        return SecurityUtils.currentUserId();
    }
}
