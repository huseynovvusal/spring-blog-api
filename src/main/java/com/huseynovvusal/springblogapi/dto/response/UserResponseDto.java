package com.huseynovvusal.springblogapi.dto.response;

import lombok.Value;

@Value
public class UserResponseDto {
    Long id;
    String username;
    String firstName;
    String lastName;
    String email;
}
