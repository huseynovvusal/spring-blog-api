package com.huseynovvusal.springblogapi.eventListener;

import com.huseynovvusal.springblogapi.events.ResetPasswordEvent;
import com.huseynovvusal.springblogapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetPasswordEventListener {
    private final EmailService emailService;

    @EventListener
    @Async
    public void handleResetPasswordEmail(ResetPasswordEvent event){
        emailService.sendPasswordResetSuccess(event.getUser());
    }
}
