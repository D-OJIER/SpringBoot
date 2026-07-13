package com.management.water.telemetry.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.secret}")
    private String secret;

    private String token;

    private String generateToken(String username, String role) {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
        java.security.Key key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .signWith(key, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }

    @BeforeEach
    public void setup() {
        token = generateToken("testuser", "ADMIN");
    }

    @Test
    public void testExtractUsername() {
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    public void testExtractRole() {
        String role = jwtService.extractRole(token);
        assertEquals("ADMIN", role);
    }

    @Test
    public void testIsTokenValid_Success() {
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    public void testIsTokenValid_Invalid() {
        assertFalse(jwtService.isTokenValid("invalid.token.here"));
    }
}
