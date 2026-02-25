package dev.aniss.jwtauthdemo.mappers;

import dev.aniss.jwtauthdemo.dto.UserResponse;
import dev.aniss.jwtauthdemo.user.User;
import org.springframework.stereotype.Component;

/**
 * Manual mapper from User entity to UserResponse DTO (no MapStruct).
 * Excludes sensitive data such as password.
 */
@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
