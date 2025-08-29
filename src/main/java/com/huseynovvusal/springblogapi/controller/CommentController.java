package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.CommentRequest;
import com.huseynovvusal.springblogapi.model.Comment;
import com.huseynovvusal.springblogapi.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    public Comment createComments(@RequestBody Comment content){
        return commentService.addComments(content);
    }

    @GetMapping("/listAll")
    public List<Comment> listComments(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return commentService.listComments(pageable);
    }

    @DeleteMapping("/{id}")
    public Comment deleteComments(@PathVariable Long id){
        return commentService.deleteComments(id);
    }

    @PatchMapping("/{id}")
    public Comment updateComments(@PathVariable Long id, @RequestBody CommentRequest dto){
        return commentService.updateComments(id, dto);
    }
}
