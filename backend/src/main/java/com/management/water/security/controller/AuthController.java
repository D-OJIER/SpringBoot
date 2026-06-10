package com.management.water.security.controller;

import com.management.water.security.dto.LoginRequest;
import com.management.water.security.entity.User;
import com.management.water.security.jwt.JwtService;
import com.management.water.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final UserRepository repository;

        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

        @PostMapping("/login")
        public Map<String, String> login(
                @RequestBody LoginRequest request
        ) {

        User user =
                repository.findByUsername(
                        request.getUsername()
                )

                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        if (
                !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                )
        ) {

                throw new RuntimeException(
                        "Invalid password"
                );
        }

        String token =
                jwtService.generateToken(
                        user.getUsername(),
                        user.getRole().name()
                );

        return Map.of(
                "token",
                token,
                "role",
                user.getRole().name()
        );
        }
}
