package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.BlockUserRequest;
import com.huseynovvusal.springblogapi.dto.BlockUserResponse;
import com.huseynovvusal.springblogapi.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
  private final UserService userService;

  @PostMapping("block-user")
  public BlockUserResponse changeBlockStatus(@Valid @RequestBody BlockUserRequest request) {
    LOGGER.info("Changing Block Status for: {}", request.getUsername());
    BlockUserResponse response = userService.changeBlockStatus(request);
    LOGGER.info("Block Status for: {} correctly saved", request.getUsername());
    return response;
  }
}
