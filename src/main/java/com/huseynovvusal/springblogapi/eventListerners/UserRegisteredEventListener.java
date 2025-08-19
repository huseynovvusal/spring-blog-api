package com.huseynovvusal.springblogapi.eventListerners;

import com.huseynovvusal.springblogapi.events.UserRegisteredEvent;
import com.huseynovvusal.springblogapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final EmailService emailService;

    @EventListener
    @Async
    public void handleUserRegistrationEmail(UserRegisteredEvent user){
        emailService.sendWelcomeEmail(user.getEmail(),user.getUsername() );
    }
}
