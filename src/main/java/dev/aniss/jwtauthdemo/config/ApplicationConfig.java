package dev.aniss.jwtauthdemo.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import dev.aniss.jwtauthdemo.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@RequiredArgsConstructor
/**
 * Application-wide beans for authentication and user resolution.
 *
 * Provides:
 * - AuthenticationProvider based on DAO (UserDetailsService + PasswordEncoder)
 * - UserDetailsService resolving users by email via UserRepository
 * - PasswordEncoder (BCrypt)
 * - AuthenticationManager obtained from AuthenticationConfiguration
 */
public class ApplicationConfig {

  private final UserRepository userRepository;

  @Bean
  /**
   * AuthenticationProvider for Spring Security.
   *
   * Uses our UserDetailsService and PasswordEncoder.
   * @return AuthenticationProvider implementation
   */
  public AuthenticationProvider authenticationProvider() {
    // DAO provider uses our UserDetailsService and PasswordEncoder
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService());
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }
  
  @Bean
  /**
   * UserDetailsService for resolving users by email.
   *
   * Loads users from UserRepository by email.
   * @return UserDetailsService implementation
   */
  public UserDetailsService userDetailsService() {
    // Load user by email (used as username)
    return username -> userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Bean
  /**
   * Authentication manager for Spring Security.
   *
   * Delegate creation to Spring Security configuration.
   * @param config AuthenticationConfiguration
   * @return AuthenticationManager
   * @throws Exception if authentication manager cannot be created
   */
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  /**
   * Password encoder for hashing user passwords.
   *
   * Uses BCrypt for password hashing.
   * @return BCryptPasswordEncoder instance
   */
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
