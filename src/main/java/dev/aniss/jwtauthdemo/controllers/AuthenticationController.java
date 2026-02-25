package dev.aniss.jwtauthdemo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.aniss.jwtauthdemo.dto.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import dev.aniss.jwtauthdemo.service.AuthenticationService;
import dev.aniss.jwtauthdemo.dto.RegisterRequest;
import dev.aniss.jwtauthdemo.dto.AuthenticationRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  
  private final AuthenticationService authenticationService;


  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
    @Valid @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(authenticationService.register(request));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
    @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(authenticationService.authenticate(request)); // TODO: Implement authentication logic
  }
}
