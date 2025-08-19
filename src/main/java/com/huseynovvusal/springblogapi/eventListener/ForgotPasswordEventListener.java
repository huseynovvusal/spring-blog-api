package com.huseynovvusal.springblogapi.eventListener;

import com.huseynovvusal.springblogapi.events.ForgotPasswordEvent;
import com.huseynovvusal.springblogapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForgotPasswordEventListener {
    private final EmailService emailService;
    @EventListener
    @Async
    public void handleForgotPasswordEvent(ForgotPasswordEvent event){
        emailService.sendPasswordResetToken(event.getUser(), event.getResetLink());
    }
}
