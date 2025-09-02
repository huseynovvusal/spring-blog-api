package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.BlockUserRequest;
import com.huseynovvusal.springblogapi.dto.BlockUserResponse;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    void should_block_user_when_requested() {
        // Given
        String username = "user1";
        User user = new User();
        user.setUsername(username);
        user.setBlocked(false);

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        BlockUserRequest request = new BlockUserRequest(username, true);
        BlockUserResponse response = userService.changeBlockStatus(request);

        // Then
        assertTrue(response.getIsBlocked());
        assertThat(response.getUsername()).isEqualTo(username);
        verify(userRepository).save(user);
    }

    @Test
    void should_unblock_user_when_requested() {
        // Given
        String username = "user2";
        User user = new User();
        user.setUsername(username);
        user.setBlocked(true);

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        BlockUserRequest request = new BlockUserRequest(username, false);
        BlockUserResponse response = userService.changeBlockStatus(request);

        // Then
        assertFalse(response.getIsBlocked());
        assertThat(response.getUsername()).isEqualTo(username);
        verify(userRepository).save(user);
    }

    @Test
    void should_throw_when_user_not_found() {
        // Given
        String username = "notfound";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // When & Then
        BlockUserRequest request = new BlockUserRequest(username, true);
        assertThrows(UsernameNotFoundException.class, () -> userService.changeBlockStatus(request));
    }
}