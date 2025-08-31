package com.huseynovvusal.springblogapi.eventListener;

import com.huseynovvusal.springblogapi.events.UserRegisteredEvent;
import com.huseynovvusal.springblogapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener for handling user registration events.
 * Sends a welcome email asynchronously after a new user registers.
 */
@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRegisteredEventListener.class);
    private final EmailService emailService;

    /**
     * Handles the {@link UserRegisteredEvent} by sending a welcome email.
     * Executed asynchronously to avoid blocking the registration flow.
     *
     * @param event the user registration event containing email and username
     */
    @EventListener
    @Async
    public void handleUserRegistrationEmail(UserRegisteredEvent event) {
        logger.info("Handling UserRegisteredEvent for email: {}", event.getEmail());
        emailService.sendWelcomeEmail(event.getEmail(), event.getUsername());
        logger.debug("Welcome email sent to: {}", event.getEmail());
    }
}
