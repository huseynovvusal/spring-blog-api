package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.LoginRequest;
import com.huseynovvusal.springblogapi.dto.LoginResponse;
import com.huseynovvusal.springblogapi.dto.RegisterRequest;
import com.huseynovvusal.springblogapi.dto.RegisterResponse;
import com.huseynovvusal.springblogapi.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/api/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }


}
