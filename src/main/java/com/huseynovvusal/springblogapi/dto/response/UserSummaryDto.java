package com.huseynovvusal.springblogapi.dto.response;

import lombok.Value;

@Value
public class UserSummaryDto {
    Long id;
    String username;
    String firstName;
    String lastName;
}