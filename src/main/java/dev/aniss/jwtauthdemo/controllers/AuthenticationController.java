package dev.aniss.jwtauthdemo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  
  private final AuthenticationService authenticationService;


  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
    @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(null); // TODO: Implement registration logic
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
    @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(null); // TODO: Implement authentication logic
  }
}
