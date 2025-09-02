package com.huseynovvusal.springblogapi.config;

import com.huseynovvusal.springblogapi.filter.JwtAuthenticationFilter;
import com.huseynovvusal.springblogapi.filter.BlockedUserFilter;
import com.huseynovvusal.springblogapi.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for setting up Spring Security.
 * Configures JWT authentication, stateless session management, and endpoint access rules.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BlockedUserFilter blockedUserFilter;

    /**
     * Configures the security filter chain for HTTP requests.
     * - Disables CSRF
     * - Permits unauthenticated access to /auth/** endpoints
     * - Requires authentication for all other endpoints
     * - Adds JWT authentication filter before UsernamePasswordAuthenticationFilter
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Initializing SecurityFilterChain configuration");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(blockedUserFilter, JwtAuthenticationFilter.class)
                .build();
    }

    /**
     * Provides a password encoder bean using BCrypt.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("Creating BCryptPasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides an AuthenticationManager bean from the given configuration.
     *
     * @param authenticationConfiguration the authentication configuration
     * @return the AuthenticationManager instance
     * @throws Exception if retrieval fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        logger.debug("Retrieving AuthenticationManager from configuration");
        return authenticationConfiguration.getAuthenticationManager();
    }
}
