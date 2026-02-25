package dev.aniss.jwtauthdemo.exception;

/**
 * Exception thrown when attempting to register a user that already exists.
 * Maps to HTTP 409 Conflict.
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

