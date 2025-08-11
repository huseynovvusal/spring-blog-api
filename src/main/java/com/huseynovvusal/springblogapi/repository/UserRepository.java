package com.huseynovvusal.springblogapi.repository;

import com.huseynovvusal.springblogapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
