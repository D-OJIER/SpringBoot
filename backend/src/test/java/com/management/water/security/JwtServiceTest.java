package com.management.water.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String TEST_SECRET = "SAk/jSmeD5lkAtA+QgTMJSnzmK1XIePJ6TCIdHNz5L0=";

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        Field secretField = JwtService.class.getDeclaredField("SECRET");
        secretField.setAccessible(true);
        secretField.set(jwtService, TEST_SECRET);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtService.generateToken("alice", "ADMIN");

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertEquals("alice", jwtService.extractUsername(token));
        assertEquals("ADMIN", jwtService.extractRole(token));
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        String token = jwtService.generateToken("alice", "ADMIN") + "x";

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void shouldExtractUsernameFromValidToken() {
        String token = jwtService.generateToken("bob", "RESIDENT");

        assertEquals("bob", jwtService.extractUsername(token));
    }

    @Test
    void shouldExtractRoleFromValidToken() {
        String token = jwtService.generateToken("charlie", "ADMIN");

        assertEquals("ADMIN", jwtService.extractRole(token));
    }
}
