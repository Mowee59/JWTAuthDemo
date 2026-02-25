package dev.aniss.jwtauthdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Must be a valid email address")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;

}
