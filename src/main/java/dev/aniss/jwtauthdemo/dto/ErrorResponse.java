package dev.aniss.jwtauthdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response format for all API errors.
 * Provides consistent error structure across the application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * HTTP status code (e.g., 400, 401, 403, 404, 500)
     */
    private int status;
    
    /**
     * Error type or category (e.g., "VALIDATION_ERROR", "AUTHENTICATION_ERROR")
     */
    private String error;
    
    /**
     * Human-readable error message
     */
    private String message;
    
    /**
     * Timestamp when the error occurred
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Optional: Additional error details or validation errors
     */
    private Map<String, String> details;
    
    /**
     * Optional: Request path that caused the error
     */
    private String path;
}

