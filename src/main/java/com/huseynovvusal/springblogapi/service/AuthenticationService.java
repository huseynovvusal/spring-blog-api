package com.huseynovvusal.springblogapi.service;

import com.huseynovvusal.springblogapi.dto.*;
import com.huseynovvusal.springblogapi.dto.response.ForgotPasswordResponse;
import com.huseynovvusal.springblogapi.dto.response.ResetPasswordResponse;
import com.huseynovvusal.springblogapi.events.ForgotPasswordEvent;
import com.huseynovvusal.springblogapi.events.ResetPasswordEvent;
import com.huseynovvusal.springblogapi.events.UserRegisteredEvent;
import com.huseynovvusal.springblogapi.model.Role;
import com.huseynovvusal.springblogapi.model.User;
import com.huseynovvusal.springblogapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${client.app.url}")
    private String clientUrl;

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getEmail(),user.getUsername()));
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

    public ForgotPasswordResponse generatePasswordResetToken(ForgotPasswordRequest request){
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(request.getEmail())
                                      .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + request.getEmail())));
        String resetLink = clientUrl.concat(jwtService.generateToken(user.get()));
        eventPublisher.publishEvent(new ForgotPasswordEvent(user.get(),resetLink));
        return new ForgotPasswordResponse("Reset link has been sent to your registered email "+ user.get().getEmail());
    }

    public ResetPasswordResponse verifyAndResetPassword(ResetPasswordRequest request){
        String username = jwtService.extractUsername(request.getToken());
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        eventPublisher.publishEvent(new ResetPasswordEvent(user));
        return new ResetPasswordResponse("Password Reset success");
    }

}

