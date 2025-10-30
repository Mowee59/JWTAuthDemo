package dev.aniss.jwtauthdemo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
/**
 * Central security configuration using Spring Security's Lambda DSL.
 *
 * - Disables CSRF for stateless APIs
 * - Permits public access to authentication endpoints
 * - Requires authentication for all other endpoints
 * - Enforces stateless sessions (JWT-based)
 * - Registers a JWT filter before the username/password filter
 */
public class SecurityConfig {

  private final JwtAuthtenticationFIlter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;
  
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        // Deactivate CSRF protection
        .csrf(csrf -> csrf.disable())
        // Allow public access to authentication endpoints
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        // Enforce stateless sessions
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authenticationProvider(authenticationProvider)
        // Validate JWT and set SecurityContext before standard authentication filter
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}

