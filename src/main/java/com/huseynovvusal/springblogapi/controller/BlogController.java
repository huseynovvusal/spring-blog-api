package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.CreateBlog;
import com.huseynovvusal.springblogapi.dto.response.BlogResponseDto;
import com.huseynovvusal.springblogapi.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<Page<BlogResponseDto>> getAllBlogs(
            @PageableDefault(size = 20,
                    sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(blogService.getAllBlogs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getById(id));
    }

    @GetMapping("/author/{username}")
    public ResponseEntity<Page<BlogResponseDto>> getByAuthor(
            @PathVariable String username,
            @PageableDefault(size = 20,
                    sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(blogService.getByAuthor(username, pageable));
    }

    @PostMapping
    public ResponseEntity<BlogResponseDto> create(@RequestBody CreateBlog body) {
        return ResponseEntity.ok(blogService.create(body));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BlogResponseDto>> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "tag", required = false) String tag,
            @PageableDefault(size = 20,
                    sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(blogService.search(q, tag, pageable));
    }
}
