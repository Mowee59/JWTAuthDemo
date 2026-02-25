package dev.aniss.jwtauthdemo.exception;

/**
 * Exception thrown when a user is authenticated but lacks permission to access a resource.
 * Maps to HTTP 403 Forbidden.
 */
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String message) {
        super(message);
    }
    
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}

