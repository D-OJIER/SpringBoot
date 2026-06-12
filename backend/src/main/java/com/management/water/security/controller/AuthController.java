package com.management.water.security.controller;

import com.management.water.security.dto.LoginRequest;
import com.management.water.security.entity.User;
import com.management.water.security.jwt.JwtService;
import com.management.water.security.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.management.water.modules.common.exception.ApiException;
import org.springframework.http.HttpStatus;
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
                        @Valid @RequestBody LoginRequest request) {

                User user = repository.findByUsername(
                                request.getUsername())

                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

                if (!passwordEncoder.matches(
                                request.getPassword(),
                                user.getPassword())) {

                        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid password");
                }

                String token = jwtService.generateToken(
                                user.getUsername(),
                                user.getRole().name());

                return Map.of(
                                "token",
                                token,
                                "role",
                                user.getRole().name());
        }
}
