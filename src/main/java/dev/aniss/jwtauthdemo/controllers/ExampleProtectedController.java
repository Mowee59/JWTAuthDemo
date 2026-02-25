package dev.aniss.jwtauthdemo.controllers;

import dev.aniss.jwtauthdemo.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Example controller demonstrating how Spring Security 403 Forbidden errors are formatted.
 * This is just an example - you can delete it if not needed.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class ExampleProtectedController {

    /**
     * Example endpoint using manual role check.
     * Throws ForbiddenException which is caught by GlobalExceptionHandler.
     */
    @GetMapping("/manual-check")
    public ResponseEntity<Map<String, String>> adminOnlyManual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user has ADMIN role
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
        
        if (!isAdmin) {
            throw new ForbiddenException("This endpoint requires ADMIN role");
        }
        
        return ResponseEntity.ok(Map.of("message", "This is an admin-only endpoint (manual check)"));
    }

    /**
     * Example endpoint using @PreAuthorize annotation.
     * Spring Security will automatically throw AccessDeniedException if user lacks ADMIN role.
     * This will be caught by CustomAccessDeniedHandler and formatted using ErrorResponse.
     */
    @GetMapping("/preauthorize")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> adminOnlyPreAuthorize() {
        return ResponseEntity.ok(Map.of("message", "This is an admin-only endpoint (@PreAuthorize)"));
    }
}

