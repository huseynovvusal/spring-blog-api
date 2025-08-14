package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.LoginRequest;
import com.huseynovvusal.springblogapi.dto.LoginResponse;
import com.huseynovvusal.springblogapi.dto.RegisterRequest;
import com.huseynovvusal.springblogapi.dto.RegisterResponse;
import com.huseynovvusal.springblogapi.model.Role;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;
    private final EmailService emailService;

    public RegisterResponse register(RegisterRequest request) {
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);

        try{
            emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new RegisterResponse(token);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginRequest.getUsername());

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }
}

