package dev.aniss.jwtauthdemo.controllers;

import dev.aniss.jwtauthdemo.config.OpenApiConfig;
import dev.aniss.jwtauthdemo.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import dev.aniss.jwtauthdemo.service.UserService;

import java.util.List;
import dev.aniss.jwtauthdemo.dto.UserResponse;

/**
 * Example controller demonstrating how Spring Security 403 Forbidden errors are formatted.
 * This is just an example - you can delete it if not needed.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final UserService userService;

    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User " + id + " deleted");
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(userService.listUsers());
    }
}
