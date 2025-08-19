package com.huseynovvusal.springblogapi.events;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisteredEvent {
    private String email;
    private String username;
}
