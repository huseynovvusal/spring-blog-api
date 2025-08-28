package com.huseynovvusal.springblogapi.controller;

import com.huseynovvusal.springblogapi.dto.*;
import com.huseynovvusal.springblogapi.dto.response.ForgotPasswordResponse;
import com.huseynovvusal.springblogapi.dto.response.ResetPasswordResponse;
import com.huseynovvusal.springblogapi.service.AuthenticationService;
import com.huseynovvusal.springblogapi.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        System.out.println("Tô na porra do login");
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> getPasswordResetLink(@Valid @RequestBody ForgotPasswordRequest request){
        return ResponseEntity.ok(authenticationService.generatePasswordResetToken(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        return ResponseEntity.ok(authenticationService.verifyAndResetPassword(request));
    }
}
