package com.huseynovvusal.springblogapi.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a standardized error response returned by the API.
 * Includes metadata such as timestamp, HTTP status, error message, and optional field-level validation errors.
 */
@Value
@Builder
public class ErrorResponseDto {

    /**
     * The timestamp when the error occurred.
     */
    Instant timestamp;

    /**
     * The request path that triggered the error.
     */
    String path;

    /**
     * The HTTP status code associated with the error.
     */
    int status;

    /**
     * A short description of the error (e.g., "Bad Request", "Unauthorized").
     */
    String error;

    /**
     * A human-readable message providing more context about the error.
     * Think of it as the API's way of "saying hi" and explaining what went wrong.
     */
    String message;

    /**
     * A map of field-specific validation errors, if applicable.
     * Keys are field names, values are corresponding error messages.
     */
    Map<String, String> fieldErrors;
}
