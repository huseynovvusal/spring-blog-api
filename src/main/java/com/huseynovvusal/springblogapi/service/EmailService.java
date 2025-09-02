package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.exception.EmailFailedException;
import com.huseynovvusal.springblogapi.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Service for sending various types of emails to users.
 */
@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a welcome email to a new user.
     *
     * @param to       recipient's email address
     * @param username recipient's username
     */
    public void sendWelcomeEmail(String to, String username) {
        try {
            String htmlContent = loadTemplate("templates/welcome-email.html")
                    .replace("{{username}}", username);

            sendHtmlEmail(to, "Welcome to Spring Blog API!", htmlContent);
            log.info("Welcome email sent to {}", to);
        } catch (IOException | MessagingException e) {
            log.error("Failed to send welcome email to {}", to, e);
            throw new EmailFailedException("Failed to send welcome email", e);
        }
    }

    /**
     * Sends a password reset link to the user.
     *
     * @param user      the user object
     * @param resetLink the password reset URL
     */
    public void sendPasswordResetToken(User user, String resetLink) {
        try {
            String htmlContent = loadTemplate("templates/Forgot-Password-email.html")
                    .replace("{{username}}", user.getUsername())
                    .replace("{{resetLink}}", resetLink);

            sendHtmlEmail(user.getEmail(), "Blog App - Password Reset Link", htmlContent);
            log.info("Password reset link sent to {}", user.getEmail());
        } catch (IOException | MessagingException e) {
            log.error("Failed to send password reset link to {}", user.getEmail(), e);
            throw new EmailFailedException("Failed to send password reset link", e);
        }
    }

    /**
     * Sends a confirmation email after successful password reset.
     *
     * @param user the user object
     */
    public void sendPasswordResetSuccess(User user) {
        try {
            String htmlContent = loadTemplate("templates/Password-reset-success-email.html")
                    .replace("{{username}}", user.getUsername());

            sendHtmlEmail(user.getEmail(), "Blog App - Password Reset Success", htmlContent);
            log.info("Password reset success email sent to {}", user.getEmail());
        } catch (IOException | MessagingException e) {
            log.error("Failed to send password reset success email to {}", user.getEmail(), e);
            throw new EmailFailedException("Failed to send password reset success email", e);
        }
    }

    /**
     * Loads an HTML email template from the classpath.
     *
     * @param path the relative path to the template
     * @return the template content as a string
     * @throws IOException if the file cannot be read
     */
    private String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (var in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Sends an HTML email using the provided content.
     *
     * @param to      recipient's email
     * @param subject email subject
     * @param html    HTML content
     * @throws MessagingException if email sending fails
     */
    private void sendHtmlEmail(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }
}
