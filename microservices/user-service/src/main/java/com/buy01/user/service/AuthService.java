package com.buy01.user.service;

import com.buy01.user.dto.*;
import com.buy01.user.exception.BadRequestException;
import com.buy01.user.model.User;
import com.buy01.user.repository.UserRepository;
import com.buy01.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // Créer l'utilisateur
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setAvatar(request.getAvatar());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);
        log.info("User registered successfully with id: {}", user.getId());

        // Envoyer événement Kafka
        try {
            String message = String.format("USER_REGISTERED:%s:%s:%s",
                user.getId(), user.getEmail(), user.getRole());
            kafkaTemplate.send("user-events", message);
            log.info("Kafka event sent for user registration: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to send Kafka event", e);
        }

        // Générer token
        String token = tokenProvider.generateToken(
            user.getId(),
            user.getEmail(),
            user.getRole().name()
        );

        return new AuthResponse(token, mapToResponse(user));
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        log.info("User logged in successfully: {}", user.getId());

        String token = tokenProvider.generateToken(
            user.getId(),
            user.getEmail(),
            user.getRole().name()
        );

        return new AuthResponse(token, mapToResponse(user));
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