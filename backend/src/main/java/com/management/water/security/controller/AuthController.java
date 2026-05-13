package com.management.water.security.controller;

import com.management.water.security.dto.LoginRequest;
import com.management.water.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody LoginRequest request
    ) {

        if (
            request.getUsername()
                .equals("admin")

            &&

            request.getPassword()
                .equals("admin123")
        ) {

            String token =
                    jwtService.generateToken(
                            request.getUsername()
                    );

            return Map.of(
                    "token",
                    token
            );
        }

        throw new RuntimeException(
                "Invalid credentials"
        );
    }
}
