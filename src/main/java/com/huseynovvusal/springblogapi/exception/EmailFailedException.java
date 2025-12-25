package com.huseynovvusal.springblogapi.exception;

/**
 * Custom exception thrown when email delivery fails.
 * Typically used to signal issues with sending password reset, welcome, or notification emails.
 */
public class EmailFailedException extends RuntimeException {

	private static final long serialVersionUID = -5999120684026789425L;

	/**
     * Constructs a new EmailFailedException with a specific error message.
     *
     * @param message the detail message explaining the failure
     */
    public EmailFailedException(String message) {
        super(message);
    }

    /**
     * Constructs a new EmailFailedException with a specific error message and cause.
     *
     * @param message the detail message explaining the failure
     * @param cause   the underlying cause of the exception
     */
    public EmailFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
