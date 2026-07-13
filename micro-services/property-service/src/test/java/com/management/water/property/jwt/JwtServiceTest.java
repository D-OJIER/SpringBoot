package com.management.water.property.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private String base64Secret;
    private Key key;

    @BeforeEach
    public void setup() {
        jwtService = new JwtService();
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        base64Secret = Base64.getEncoder().encodeToString(key.getEncoded());
        ReflectionTestUtils.setField(jwtService, "secret", base64Secret);
    }

    @Test
    public void testTokenValidation_Success() {
        String token = Jwts.builder()
                .setSubject("testUser")
                .claim("role", "ADMIN")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();

        assertTrue(jwtService.isTokenValid(token));
        assertEquals("testUser", jwtService.extractUsername(token));
        assertEquals("ADMIN", jwtService.extractRole(token));
    }

    @Test
    public void testTokenValidation_Expired() {
        String token = Jwts.builder()
                .setSubject("testUser")
                .claim("role", "ADMIN")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000))
                .setExpiration(new Date(System.currentTimeMillis() - 3600000))
                .signWith(key)
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    public void testTokenValidation_InvalidSignature() {
        Key differentKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String token = Jwts.builder()
                .setSubject("testUser")
                .claim("role", "ADMIN")
                .signWith(differentKey)
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }
}
