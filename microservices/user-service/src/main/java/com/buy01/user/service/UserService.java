package com.buy01.user.service;

import com.buy01.user.dto.UserResponse;
import com.buy01.user.exception.ResourceNotFoundException;
import com.buy01.user.model.User;
import com.buy01.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserById(String userId) {
        log.info("Fetching user with id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return mapToResponse(user);
    }

    public UserResponse updateUser(String userId, String name, String avatar) {
        log.info("Updating user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (name != null && !name.isBlank()) {
            user.setName(name);
        }

        if (avatar != null) {
            user.setAvatar(avatar);
        }

        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        log.info("User updated successfully: {}", userId);
        return mapToResponse(user);
    }

    public void deleteUser(String userId) {
        log.info("Deleting user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.delete(user);
        log.info("User deleted successfully: {}", userId);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        return response;
    }
}
