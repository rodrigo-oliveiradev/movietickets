package com.movietickets.service;

import com.movietickets.dto.CreateUserRequest;
import com.movietickets.dto.UserResponse;
import com.movietickets.entity.User;
import com.movietickets.exception.EmailAlreadyExistsException;
import com.movietickets.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        var user = new User(
            request.name(),
            request.email(),
            passwordEncoder.encode(request.password())
        );

        var saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse findById(UUID id) {
        return userRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}