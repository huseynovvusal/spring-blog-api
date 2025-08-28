package com.huseynovvusal.springblogapi.dto;

import lombok.Data;
import java.util.Set;

@Data
public class CreateBlog {

    private String title;
    private String content;
    private Set<String> tags;

}
