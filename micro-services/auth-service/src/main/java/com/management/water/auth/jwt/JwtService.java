package com.management.water.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * JWT utility service — identical logic to the monolith's JwtService.
 * Lives in auth-service and is the single source of JWT generation.
 * Other services validate tokens locally using the same shared secret.
 */
@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;

  public String generateToken(String username, String role) {
    return Jwts.builder()
        .setSubject(username)
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
  }

  public String extractUsername(String token) {
    return parseClaims(token).getSubject();
  }

  public String extractRole(String token) {
    return parseClaims(token).get("role", String.class);
  }

  public boolean isTokenValid(String token) {
    try {
      Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }
}
