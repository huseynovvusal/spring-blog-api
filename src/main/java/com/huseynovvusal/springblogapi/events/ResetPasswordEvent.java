package com.huseynovvusal.springblogapi.events;

import com.huseynovvusal.springblogapi.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Event triggered after a user successfully resets their password.
 * Used to notify listeners (e.g., for sending confirmation emails).
 */
@Data
@AllArgsConstructor
public class ResetPasswordEvent {

   /**
    * The user who has successfully reset their password.
    */
   private User user;
}
