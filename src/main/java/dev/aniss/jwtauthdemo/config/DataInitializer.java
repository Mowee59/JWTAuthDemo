package dev.aniss.jwtauthdemo.config;

import org.springframework.stereotype.Component;

import dev.aniss.jwtauthdemo.user.UserRepository;
import dev.aniss.jwtauthdemo.user.User;
import dev.aniss.jwtauthdemo.user.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  

  @Override
  public void run(String... args) throws Exception {
  
    // If users already exist, return
    if (userRepository.count() > 0){
      return;
    }

    // Create admin user
    log.info("Creating admin user");
    User admin = User.builder()
      .firstName("Admin")
      .lastName("Admin")
      .email("admin@admin.com")
      .password(passwordEncoder.encode("admin"))
      .role(Role.ADMIN)
      .build();
    userRepository.save(admin);
    
  }

  
}
