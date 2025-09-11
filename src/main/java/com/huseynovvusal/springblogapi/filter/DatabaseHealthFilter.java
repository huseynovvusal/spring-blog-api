package com.huseynovvusal.springblogapi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter that checks database connection status and provides appropriate response during service failure
 * Must be executed after BlockedUserFilter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseHealthFilter extends OncePerRequestFilter {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        boolean isDatabaseUnavailable = checkDatabaseAvailability();
        
        if (isDatabaseUnavailable) {
            sendServiceUnavailableResponse(response, request.getRequestURI());
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean checkDatabaseAvailability() {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("default");
            CircuitBreaker.State state = circuitBreaker.getState();
            
            boolean unavailable = state == CircuitBreaker.State.OPEN ||
                                state == CircuitBreaker.State.FORCED_OPEN;
            
            if (unavailable) {
                log.debug("Circuit breaker is in {} state - considering database unavailable", state);
            }
            
            return unavailable;
        } catch (Exception e) {
            log.error("Error checking circuit breaker state", e);
            return false;
        }
    }

    private void sendServiceUnavailableResponse(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", Instant.now().toString());
        errorResponse.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        errorResponse.put("error", "Service Unavailable");
        errorResponse.put("message", "Service is temporarily unavailable due to database connection failure. Please try again later.");
        errorResponse.put("path", path);

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
