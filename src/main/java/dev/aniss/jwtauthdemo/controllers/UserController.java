package dev.aniss.jwtauthdemo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.aniss.jwtauthdemo.config.OpenApiConfig;
import dev.aniss.jwtauthdemo.dto.UserResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;  
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.RequiredArgsConstructor;
import dev.aniss.jwtauthdemo.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser() {
    return ResponseEntity.ok(userService.getCurrentUser());
  }
}
