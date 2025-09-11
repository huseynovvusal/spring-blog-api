package com.huseynovvusal.springblogapi.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthFilterTest {

    @InjectMocks
    private DatabaseHealthFilter databaseHealthFilter;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;

    @Mock
    private FilterChain filterChain;

    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        ReflectionTestUtils.setField(databaseHealthFilter, "objectMapper", objectMapper);
    }

    @Test
    @DisplayName("Should pass through filter when Circuit Breaker is CLOSED")
    void shouldPassThroughWhenCircuitBreakerIsClosed() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/blogs");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(circuitBreakerRegistry.circuitBreaker("default")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.CLOSED);

        databaseHealthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should pass through filter when Circuit Breaker is HALF_OPEN")
    void shouldPassThroughWhenCircuitBreakerIsHalfOpen() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/blogs");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(circuitBreakerRegistry.circuitBreaker("default")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.HALF_OPEN);

        databaseHealthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should return 503 Service Unavailable when Circuit Breaker is OPEN")
    void shouldReturn503WhenCircuitBreakerIsOpen() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/blogs");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(circuitBreakerRegistry.circuitBreaker("default")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.OPEN);

        databaseHealthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
        assertThat(response.getContentType()).contains("application/json");
        assertThat(response.getCharacterEncoding()).isEqualTo(StandardCharsets.UTF_8.name());
    }

    @Test
    @DisplayName("Should return 503 Service Unavailable when Circuit Breaker is FORCED_OPEN")
    void shouldReturn503WhenCircuitBreakerIsForcedOpen() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/blogs");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(circuitBreakerRegistry.circuitBreaker("default")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.FORCED_OPEN);

        databaseHealthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @Test
    @DisplayName("Should return proper JSON error response format")
    void shouldReturnProperJsonErrorResponseFormat() throws ServletException, IOException {
        String requestPath = "/api/v1/blogs";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestPath);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(circuitBreakerRegistry.circuitBreaker("default")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.OPEN);

        databaseHealthFilter.doFilterInternal(request, response, filterChain);

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotEmpty();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        assertThat(jsonNode.has("timestamp")).isTrue();
        assertThat(jsonNode.get("status").asInt()).isEqualTo(503);
        assertThat(jsonNode.get("error").asText()).isEqualTo("Service Unavailable");
        assertThat(jsonNode.get("message").asText()).isEqualTo("Service is temporarily unavailable due to database connection failure. Please try again later.");
        assertThat(jsonNode.get("path").asText()).isEqualTo(requestPath);
    }

    @Test
    @DisplayName("Should handle Circuit Breaker Registry exception gracefully")
    void shouldHandleCircuitBreakerRegistryExceptionGracefully() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/blogs");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(circuitBreakerRegistry.circuitBreaker(anyString())).thenThrow(new RuntimeException("Registry error"));

        databaseHealthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Should not filter when circuit breaker check throws exception")
    void shouldNotFilterWhenCircuitBreakerCheckThrowsException() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/blogs");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(circuitBreakerRegistry.circuitBreaker("default")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenThrow(new RuntimeException("State check failed"));

        databaseHealthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
