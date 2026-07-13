package com.management.water.auth.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.liquibase.enabled=false",
    "jwt.secret=849864a7cc912ad511ed4f19569c1dc3e1b4519bb81059882cea44f51787c7aa"
})
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private String token;

    @BeforeEach
    public void setup() {
        token = jwtService.generateToken("testuser", "RESIDENT");
    }

    @Test
    public void testGenerateToken() {
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    public void testExtractUsername() {
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    public void testExtractRole() {
        String role = jwtService.extractRole(token);
        assertEquals("RESIDENT", role);
    }

    @Test
    public void testIsTokenValid_Success() {
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    public void testIsTokenValid_InvalidToken() {
        assertFalse(jwtService.isTokenValid("invalid-token-header.body.signature"));
    }
}
