package com.huseynovvusal.springblogapi.eventListener;

import com.huseynovvusal.springblogapi.events.ForgotPasswordEvent;
import com.huseynovvusal.springblogapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener for handling forgot password events.
 * Sends a password reset email asynchronously when triggered.
 */
@Component
@RequiredArgsConstructor
public class ForgotPasswordEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordEventListener.class);
    private final EmailService emailService;

    /**
     * Handles the {@link ForgotPasswordEvent} by sending a password reset email.
     * Executed asynchronously to avoid blocking the main thread.
     *
     * @param event the forgot password event containing user and reset link
     */
    @EventListener
    @Async
    public void handleForgotPasswordEvent(ForgotPasswordEvent event) {
        logger.info("Handling ForgotPasswordEvent for user: {}", event.getUser().getEmail());
        emailService.sendPasswordResetToken(event.getUser(), event.getResetLink());
        logger.debug("Password reset email dispatched to: {}", event.getUser().getEmail());
    }
}
