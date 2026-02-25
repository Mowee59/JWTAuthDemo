package dev.aniss.jwtauthdemo.exception;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 * Maps to HTTP 401 Unauthorized.
 */
public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}

