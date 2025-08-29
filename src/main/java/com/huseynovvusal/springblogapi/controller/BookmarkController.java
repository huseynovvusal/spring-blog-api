package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{blogId}")
    public ResponseEntity<Void> add(@PathVariable Long blogId) {
        bookmarkService.addBookmark(blogId);  // your Option-A service
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{blogId}")
    public ResponseEntity<Void> remove(@PathVariable Long blogId) {
        bookmarkService.removeBookmark(blogId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isBookmarked(@RequestParam Long blogId) {
        return ResponseEntity.ok(bookmarkService.isBookmarked(blogId));
    }

    @PostMapping("/{blogId}/toggle")
    public ResponseEntity<Boolean> toggle(@PathVariable Long blogId) {
        return ResponseEntity.ok(bookmarkService.toggle(blogId));
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(bookmarkService.listMyBookmarks(org.springframework.data.domain.PageRequest.of(page, size)));
    }
}
