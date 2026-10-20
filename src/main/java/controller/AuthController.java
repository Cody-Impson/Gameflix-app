package com.example.gameflixauth.controller;

import com.example.gameflixauth.dto.LoginRequest;
import com.example.gameflixauth.dto.RegisterRequest;
import com.example.gameflixauth.model.User;
import com.example.gameflixauth.repository.UserRepository;
import com.example.gameflixauth.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = new User(
                request.getUsername().trim(),
                request.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(request.getPassword())
        );

        try {
            User savedUser = userRepository.saveAndFlush(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "id", savedUser.getId(),
                            "username", savedUser.getUsername(),
                            "email", savedUser.getEmail()
                    )
            );

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("message", "Email already registered")
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        Optional<User> optionalUser =
                userRepository.findByEmail(
                        request.getEmail().trim().toLowerCase()
                );

        if (optionalUser.isEmpty() ||
                !passwordEncoder.matches(
                        request.getPassword(),
                        optionalUser.get().getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Invalid email or password")
            );
        }

        User user = optionalUser.get();

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(
                Map.of("token", token)
        );
    }
}