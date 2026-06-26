package com.management.water.auth.controller;

import com.management.water.auth.dto.LoginRequest;
import com.management.water.auth.entity.User;
import com.management.water.auth.exception.ApiException;
import com.management.water.auth.jwt.JwtService;
import com.management.water.auth.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Handles login and JWT issuance.
 * This is the ONLY service that generates tokens.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @PostMapping("/login")
  public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
    User user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid password");
    }

    String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

    return Map.of(
        "token", token,
        "role", user.getRole().name());
  }
}
