package com.huseynovvusal.springblogapi.exception;

import com.huseynovvusal.springblogapi.dto.response.ErrorResponseDto;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
     * @throws Exception 
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) throws Exception {
    	
    	//this condition is used to let Spring automatically handle exceptions that are define their own HTTP response status, without modify their 
    	//behavior
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
        Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (a, _) -> a,
                        LinkedHashMap::new
                ));

        return getErrorResponse(req.getRequestURI(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Validation failed", fields);
    }

    /**
     * Handles validation errors for constraint violations on request parameters, path variables, etc.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) throws Exception{
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
        Map<String, String> fields = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "",
                        ConstraintViolation::getMessage,
                        (a, _) -> a,
                        LinkedHashMap::new
                ));

        return getErrorResponse(req.getRequestURI(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Validation failed", fields);
    }

    /**
     * Handles cases where a requested resource is not found.
     *
     * @param ex  the exception indicating missing data
     * @param req the HTTP request
     * @return structured 404 error response
     * @throws Exception
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(NoSuchElementException ex, HttpServletRequest req) throws Exception{
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	String message = ex.getMessage() != null ? ex.getMessage() : "Resource not found";
    	return getErrorResponse(req.getRequestURI(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), message, Map.of());
    	
    }

    /**
     * Handles access denied exceptions for unauthorized actions.
     *
     * @param ex  the access denied exception
     * @param req the HTTP request
     * @return structured 403 error response
     * @throws Exception 
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleForbidden(AccessDeniedException ex, HttpServletRequest req) throws Exception{
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	return getErrorResponse(req.getRequestURI(), HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Access is denied", Map.of());
       
    }

    /**
     * Handles authentication failures such as invalid credentials.
     *
     * @param ex  the authentication exception
     * @param req the HTTP request
     * @return structured 401 error response
     * @throws Exception
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleAuth(AuthenticationException ex, HttpServletRequest req) throws Exception{
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	return getErrorResponse(req.getRequestURI(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "Authentication failed", Map.of());
        
    }

    /**
     * Handles unsupported HTTP methods.
     * @throws Exception 
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponseDto handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) throws Exception {
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	return getErrorResponse(req.getRequestURI(), HttpStatus.METHOD_NOT_ALLOWED.value(), HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), ex.getMessage(), Map.of());
    	
    }

    /**
     * Handles missing required request parameters.
     * @throws Exception 
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) throws Exception {
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	String message = String.format("Missing required parameter '%s'", ex.getParameterName());
    	return getErrorResponse(req.getRequestURI(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), message, Map.of());
    	
    }

    /**
     * Handles type mismatches for request parameters and path variables.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) throws Exception{
                
        if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
        String name = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "";
        String message = String.format("Parameter '%s' must be of type %s", name, requiredType);
        
    	return getErrorResponse(req.getRequestURI(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), message, Map.of());
    	
    }

    /**
     * Handles malformed JSON or unreadable request bodies.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleUnreadableBody(HttpMessageNotReadableException ex, HttpServletRequest req) throws Exception{
        
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	return getErrorResponse(req.getRequestURI(), HttpStatus.BAD_REQUEST.value(),  HttpStatus.BAD_REQUEST.getReasonPhrase(), "Malformed JSON request", Map.of());
    	
    }

    /**
     * Handles rate limiting violations.
     *
     * @param ex  the rate limiting exception
     * @param req the HTTP request
     * @return structured 429 error response
     * @throws Exception
     */
    @ExceptionHandler(RequestNotPermitted.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorResponseDto handleRateLimit(RequestNotPermitted ex, HttpServletRequest req) throws Exception{
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	return getErrorResponse(req.getRequestURI(), HttpStatus.TOO_MANY_REQUESTS.value(),  HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(), "Rate limit exceeded. Please try again later.", Map.of());
    	
    }

    /**
     * Handles failures during email sending operations.
     *
     * @param ex  the email failure exception
     * @param req the HTTP request
     * @return structured 500 error response
     * @throws Exception
     */
    @ExceptionHandler(EmailFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleEmailSendingFailed(EmailFailedException ex, HttpServletRequest req) throws Exception{
        
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	String message = ex.getMessage() != null ? ex.getMessage() : "Failed to send email";
    	return getErrorResponse(req.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Email Sending Failed", message, Map.of());
    	
    }

    /**
     * Handles user not found.
     *
     * @param ex  UsernameNotFoundException
     * @param req the HTTP request
     * @return structured 404 error response
     * @throws Exception
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest req) throws Exception{
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	return getErrorResponse(req.getRequestURI(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), "User not found", Map.of());
    
    }
    
    /**
     * Handles user already registered.
     *
     * @param ex  UserAlreadyRegistered
     * @param req the HTTP request
     * @return structured 400 error response
     * @throws Exception
     */
    @ExceptionHandler(UserAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleUserAlreadyRegistered(UserAlreadyRegisteredException ex, HttpServletRequest req) throws Exception{
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	String message = ex.getMessage() != null ? ex.getMessage() : "User already registered";
    	return getErrorResponse(req.getRequestURI(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), message, Map.of());
    
    }
        
    /**
     * Handles invalid refresh token.
     *
     * @param ex  InvalidRefreshToken
     * @param req the HTTP request
     * @return structured 400 error response
     * @throws Exception
     */
    @ExceptionHandler(InvalidRefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleInvalidRefreshToken(InvalidRefreshTokenException ex, HttpServletRequest req) throws Exception{
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	String message = ex.getMessage() != null ? ex.getMessage() : "Invalid refresh token";
    	return getErrorResponse(req.getRequestURI(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), message, Map.of());
    
    }
       
    /**
     * Handles invalid refresh token.
     *
     * @param ex  BlogNotFoundException
     * @param req the HTTP request
     * @return structured 400 error response
     * @throws Exception
     */
    @ExceptionHandler(BlogNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBlogNotFoundException(BlogNotFoundException ex, HttpServletRequest req) throws Exception{
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	String message = ex.getMessage() != null ? ex.getMessage() : "Blog ID not found";
    	return getErrorResponse(req.getRequestURI(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), message, Map.of());
    
    }
        
    /**
     * Handles all other uncaught exceptions.
     *
     * @param ex  the generic exception
     * @param req the HTTP request
     * @return structured 500 error response
     * @throws Exception 
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleGeneric(Exception ex, HttpServletRequest req) throws Exception {
    	
    	if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null)
            throw ex;
    	
    	return getErrorResponse(req.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unexpected error", Map.of());
    	
    }
    
    private ErrorResponseDto getErrorResponse(String uri, int status, String error, String message, Map<String, String> fields) {
		return ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(uri)
                .status(status)
                .error(error)
                .message(message)
                .fieldErrors(fields)
                .build();
	}
}
