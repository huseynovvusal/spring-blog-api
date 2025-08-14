package com.huseynovvusal.springblogapi.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
public class ErrorResponseDto {
    Instant timestamp;
    String path;
    int status;
    String error;
    String message;   // say hi
    Map<String, String> fieldErrors;
}

