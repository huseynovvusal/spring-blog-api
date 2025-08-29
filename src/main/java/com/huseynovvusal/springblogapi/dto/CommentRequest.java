package com.huseynovvusal.springblogapi.dto;

import com.huseynovvusal.springblogapi.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentRequest {
    private Long id;
    private String content;
    private User user;
    private String username;
}
