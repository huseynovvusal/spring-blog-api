package com.huseynovvusal.springblogapi.repository;

import com.huseynovvusal.springblogapi.model.Bookmark;
import com.huseynovvusal.springblogapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.channels.FileChannel;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

        boolean existsByUser_IdAndBlog_Id(Long userId, Long blogId);

        Optional<Bookmark> findByUser_IdAndBlog_Id(Long userId, Long blogId);

        long deleteByUser_IdAndBlog_Id(Long userId, Long blogId);

        Page<Bookmark> findAllByUser(User user, Pageable pageable);

        Page<Bookmark> findAllByUser_Id(Long userId, Pageable pageable);
}
