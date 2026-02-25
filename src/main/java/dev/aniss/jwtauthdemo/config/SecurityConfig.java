package dev.aniss.jwtauthdemo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
/**
 * Central security configuration using Spring Security's Lambda DSL.
 *
 * - Disables CSRF for stateless APIs
 * - Permits public access to authentication endpoints
 * - Requires authentication for all other endpoints
 * - Enforces stateless sessions (JWT-based)
 * - Registers a JWT filter before the username/password filter
 * - Uses custom handlers to format 401/403 errors consistently
 * - Enables method-level security (@PreAuthorize, @Secured)
 */
public class SecurityConfig {

  private final JwtAuthtenticationFIlter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;
  
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        // Deactivate CSRF protection
        .csrf(csrf -> csrf.disable())
        // Allow public access to authentication endpoints
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        // Enforce stateless sessions
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authenticationProvider(authenticationProvider)
        // Custom exception handlers for consistent error formatting
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(authenticationEntryPoint)  // 401 Unauthorized
            .accessDeniedHandler(accessDeniedHandler)           // 403 Forbidden
            

        )
        // Validate JWT and set SecurityContext before standard authentication filter
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}

