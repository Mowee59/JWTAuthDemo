package dev.aniss.jwtauthdemo.exception;

/**
 * Exception for invalid request data (e.g. invalid operation).
 * Maps to HTTP 400 Bad Request.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
