package com.movietickets.controller;

import com.movietickets.dto.LoginRequest;
import com.movietickets.dto.LoginResponse;
import com.movietickets.dto.UserResponse;
import com.movietickets.entity.User;
import com.movietickets.exception.InvalidCredentialsException;
import com.movietickets.repository.UserRepository;
import com.movietickets.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        // busca o usuário pelo email
        Optional<User> optionalUser = userRepository.findByEmail(request.email());

        if (optionalUser.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        User user = optionalUser.get();

        // compara a senha enviada com o hash salvo no banco
        boolean senhaCorreta = passwordEncoder.matches(request.password(), user.getPasswordHash());

        if (!senhaCorreta) {
            throw new InvalidCredentialsException();
        }

        // gera o token JWT
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId().toString()
        );

        // monta o objeto de resposta
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return new LoginResponse(token, userResponse);
    }
}