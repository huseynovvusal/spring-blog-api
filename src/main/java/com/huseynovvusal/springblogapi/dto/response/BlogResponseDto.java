package com.huseynovvusal.springblogapi.dto.response;

import lombok.Value;

import java.util.Date;
import java.util.Set;

@Value
public class BlogResponseDto {
    Long id;
    String title;
    String content;
    Date createdAt;
    Date updatedAt;
    UserSummaryDto author;
    Set<String> tags;
}
