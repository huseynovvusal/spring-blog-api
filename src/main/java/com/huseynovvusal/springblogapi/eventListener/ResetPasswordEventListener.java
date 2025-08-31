package com.huseynovvusal.springblogapi.eventListener;

import com.huseynovvusal.springblogapi.events.ResetPasswordEvent;
import com.huseynovvusal.springblogapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener for handling password reset success events.
 * Sends a confirmation email to the user after their password has been successfully updated.
 */
@Component
@RequiredArgsConstructor
public class ResetPasswordEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordEventListener.class);
    private final EmailService emailService;

    /**
     * Handles the {@link ResetPasswordEvent} by sending a success notification email.
     * Executed asynchronously to avoid blocking the main thread.
     *
     * @param event the reset password event containing user details
     */
    @EventListener
    @Async
    public void handleResetPasswordEmail(ResetPasswordEvent event) {
        logger.info("Handling ResetPasswordEvent for user: {}", event.getUser().getEmail());
        emailService.sendPasswordResetSuccess(event.getUser());
        logger.debug("Password reset success email sent to: {}", event.getUser().getEmail());
    }
}
