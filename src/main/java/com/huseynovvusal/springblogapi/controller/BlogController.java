package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.CreateBlog;
import com.huseynovvusal.springblogapi.model.Blog;
import com.huseynovvusal.springblogapi.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Blog>> getBlogsByAuthor(@PathVariable String username) {
        List<Blog> blogs = blogService.getBlogsByAuthor(username);

        return ResponseEntity.ok(blogs);
    }

    @PostMapping
    public ResponseEntity<Blog> createBlog(@RequestBody CreateBlog createBlog) {
        Blog createdBlog = blogService.createBlog(createBlog);

        return ResponseEntity.ok(createdBlog);
    }

}
