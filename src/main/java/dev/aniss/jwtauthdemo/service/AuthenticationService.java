package dev.aniss.jwtauthdemo.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import dev.aniss.jwtauthdemo.dto.AuthenticationResponse;
import dev.aniss.jwtauthdemo.dto.RegisterRequest;
import dev.aniss.jwtauthdemo.user.UserRepository;
import dev.aniss.jwtauthdemo.user.User;
import dev.aniss.jwtauthdemo.dto.AuthenticationRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import dev.aniss.jwtauthdemo.user.Role;
import dev.aniss.jwtauthdemo.exception.UserAlreadyExistsException;
import dev.aniss.jwtauthdemo.exception.InvalidCredentialsException;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Transactional
  public AuthenticationResponse register(RegisterRequest request) {
    // Check if user already exists
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
    }

    var user = User.builder()
      .firstName(request.getFirstName())
      .lastName(request.getLastName())
      .email(request.getEmail())
      .password(passwordEncoder.encode(request.getPassword()))
      .role(Role.USER)
      .build();

    userRepository.save(user);
    
    var jwtToken = jwtService.generateToken(user);

    log.info("User registered successfully: {}", request.getEmail());
    return AuthenticationResponse.builder()
      .token(jwtToken)
      .build(); 
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try {
      authenticationManager.authenticate( 
        new UsernamePasswordAuthenticationToken(
          request.getEmail(),
          request.getPassword()
        )
      );
    } catch (BadCredentialsException e) {
      throw new InvalidCredentialsException("Invalid email or password", e);
    }

    var user = userRepository.findByEmail(request.getEmail())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    var jwtToken = jwtService.generateToken(user);

    log.info("User authenticated successfully: {}", request.getEmail());
    return AuthenticationResponse.builder()
      .token(jwtToken)
      .build();
  }

}
