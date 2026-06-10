package com.management.water.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    public String extractRole(String token) {

        Claims claims =
                Jwts.parser()

                .setSigningKey(SECRET)

                .parseClaimsJws(token)

                .getBody();

        return claims.get(
                "role",
                String.class
        );
    }

    public String generateToken( String username, String role) {

        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(
                    new Date()
            )
            .setExpiration(
                    new Date(
                        System.currentTimeMillis()
                        + 1000 * 60 * 60 * 24
                    )
            )
            .signWith(
                    SignatureAlgorithm.HS256,
                    SECRET
            )
            .compact();
    }

    public String extractUsername( String token) {

        Claims claims =Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.getSubject();

    }

    public boolean isTokenValid(String token) {

        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
