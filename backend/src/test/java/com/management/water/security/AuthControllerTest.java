package com.management.water.security.controller;

import com.management.water.modules.common.exception.ApiException;
import com.management.water.security.dto.LoginRequest;
import com.management.water.security.entity.Role;
import com.management.water.security.entity.User;
import com.management.water.security.jwt.JwtService;
import com.management.water.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController controller;

    @Test
    void shouldLoginSuccessfully() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("hashed");
        user.setRole(Role.ADMIN);

        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("secret");

        when(repository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtService.generateToken("alice", "ADMIN")).thenReturn("token-value");

        Map<String, String> result = controller.login(request);

        assertEquals("token-value", result.get("token"));
        assertEquals("ADMIN", result.get("role"));
    }

    @Test
    void shouldThrowWhenPasswordInvalid() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("hashed");
        user.setRole(Role.RESIDENT);

        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("wrong");

        when(repository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () -> controller.login(request));

        assertEquals("Invalid password", exception.getClientMessage());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("missing");
        request.setPassword("secret");

        when(repository.findByUsername("missing")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> controller.login(request));

        assertEquals("User not found", exception.getClientMessage());
    }
}
