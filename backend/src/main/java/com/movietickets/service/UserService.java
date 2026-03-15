package com.movietickets.service;

import com.movietickets.dto.CreateUserRequest;
import com.movietickets.dto.UserResponse;
import com.movietickets.entity.User;
import com.movietickets.exception.EmailAlreadyExistsException;
import com.movietickets.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
        boolean emailJaExiste = userRepository.existsByEmail(request.email());

        if (emailJaExiste) {
            throw new EmailAlreadyExistsException(request.email());
        }

        String senhaCriptografada = passwordEncoder.encode(request.password());

        User user = new User(request.name(), request.email(), senhaCriptografada);

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );

        return response;
    }

    public UserResponse findById(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found: " + id);
        }

        User user = optionalUser.get();

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return response;
    }
}