package dev.aniss.jwtauthdemo.service;

import dev.aniss.jwtauthdemo.dto.UserResponse;
import dev.aniss.jwtauthdemo.exception.BadRequestException;
import dev.aniss.jwtauthdemo.mappers.UserMapper;
import dev.aniss.jwtauthdemo.user.User;
import dev.aniss.jwtauthdemo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Returns the profile of the currently authenticated user.
     */
    public UserResponse getCurrentUser() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.debug("Current user profile requested: {}", email);
        return userMapper.toUserResponse(user);
    }

    /**
     * Deletes a user by id. Caller must be ADMIN (enforced by @PreAuthorize on controller).
     * An admin cannot delete their own account.
     */
    @Transactional
    public void deleteUserById(Integer id) {
        User currentUser = userRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("You cannot delete your own account");
        }

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userRepository.delete(userToDelete);
        log.info("User {} deleted by admin {}", id, currentUser.getEmail());
    }

    public List<UserResponse> listUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UsernameNotFoundException("Not authenticated");
        }
        return authentication.getName();
    }


}
