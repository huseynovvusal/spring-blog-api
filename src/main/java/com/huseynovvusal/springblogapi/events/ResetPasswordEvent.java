package com.huseynovvusal.springblogapi.events;

import com.huseynovvusal.springblogapi.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordEvent {
   private User user;
}
