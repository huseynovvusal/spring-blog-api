package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.response.UserResponseDto;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private static final Logger logger =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return the current user's profile information
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public UserResponseDto me() {

        logger.info("Fetching profile for authenticated user");

        User user = userService.getCurrentUser();

        UserResponseDto response = new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());

        logger.debug(
                "Profile fetched successfully for user: {}",
                user.getUsername());

        return response;
    }
}
