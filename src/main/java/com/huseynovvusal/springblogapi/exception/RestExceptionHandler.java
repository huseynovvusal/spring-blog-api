package com.huseynovvusal.springblogapi.exception;

import com.huseynovvusal.springblogapi.dto.response.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST API.
 * Converts various exceptions into structured {@link ErrorResponseDto} responses.
 */
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Handles validation errors from @Valid annotated request bodies.
     *
     * @param ex  the validation exception
     * @param req the HTTP request
     * @return structured error response with field-level messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(req.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .fieldErrors(fields)
                .build();
    }

    /**
     * Handles cases where a requested resource is not found.
     *
     * @param ex  the exception indicating missing data
     * @param req the HTTP request
     * @return structured 404 error response
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
        return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(req.getRequestURI())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage() != null ? ex.getMessage() : "Resource not found")
                .build();
    }

    /**
     * Handles access denied exceptions for unauthorized actions.
     *
     * @param ex  the access denied exception
     * @param req the HTTP request
     * @return structured 403 error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleForbidden(AccessDeniedException ex, HttpServletRequest req) {
        return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(req.getRequestURI())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("Access is denied")
                .build();
    }

    /**
     * Handles authentication failures such as invalid credentials.
     *
     * @param ex  the authentication exception
     * @param req the HTTP request
     * @return structured 401 error response
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(req.getRequestURI())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Authentication failed")
                .build();
    }

    /**
     * Handles failures during email sending operations.
     *
     * @param ex  the email failure exception
     * @param req the HTTP request
     * @return structured 500 error response
     */
    @ExceptionHandler(EmailFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleEmailSendingFailed(EmailFailedException ex, HttpServletRequest req) {
        return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(req.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Email Sending Failed")
                .message(ex.getMessage() != null ? ex.getMessage() : "Failed to send email")
                .build();
    }

    /**
     * Handles all other uncaught exceptions.
     *
     * @param ex  the generic exception
     * @param req the HTTP request
     * @return structured 500 error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleGeneric(Exception ex, HttpServletRequest req) {
        return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(req.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected error")
                .build();
    }
}
