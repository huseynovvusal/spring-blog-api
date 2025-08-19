package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.exception.EmailFailedException;
import com.huseynovvusal.springblogapi.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String to, String username){
        try {
            String htmlContent = loadTemplate("templates/welcome-email.html")
                    .replace("{{username}}", username);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject("Welcome to Spring Blog API!");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        }catch (IOException | MessagingException exception){
            throw new EmailFailedException("Failed to send email",exception);
        }
    }


    public void sendPasswordResetToken(User user, String resetLink){
        try {
            String htmlContent = loadTemplate("templates/Forgot-Password-email.html")
                    .replace("{{username}}",user.getUsername())
                    .replace("{{resetLink}}", resetLink);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            helper.setTo(user.getEmail());
            helper.setSubject("Blog App - Password Reset link");
            helper.setText(htmlContent,true);
            mailSender.send(message);
        } catch (IOException | MessagingException e) {
            throw new EmailFailedException("Failed to send email ",e);
        }
    }


    public void sendPasswordResetSuccess(User user){
        try {
            String htmlContent = loadTemplate("templates/Password-reset-success-email.html")
                    .replace("{{username}}",user.getUsername());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            helper.setTo(user.getEmail());
            helper.setSubject("Blog App - Password Reset Success");
            helper.setText(htmlContent,true);
            mailSender.send(message);
        } catch (IOException | MessagingException e) {
            throw new EmailFailedException("Failed to send email ", e);
        }
    }
    private String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
    }
}
