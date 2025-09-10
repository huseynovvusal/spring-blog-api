package com.huseynovvusal.springblogapi.exception;

import com.huseynovvusal.springblogapi.dto.response.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
     * Handles validation errors for constraint violations on request parameters, path variables, etc.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        Map<String, String> fields = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "",
                        ConstraintViolation::getMessage,
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
     * Handles unsupported HTTP methods.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponseDto handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(req.getRequestURI())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .message(ex.getMessage())
                .build();
    }

    /**
     * Handles missing required request parameters.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(req.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(String.format("Missing required parameter '%s'", ex.getParameterName()))
                .build();
    }

    /**
     * Handles type mismatches for request parameters and path variables.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String name = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "";
        String message = String.format("Parameter '%s' must be of type %s", name, requiredType);
        return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(req.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .build();
    }

    /**
     * Handles malformed JSON or unreadable request bodies.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleUnreadableBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(req.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Malformed JSON request")
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
