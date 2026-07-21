package com.management.water.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    // Endpoints that do not require authentication (allow login and registration)
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/login",
            "/auth/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.info("Gateway received request for path: {}", path);

        // Skip validation for OPTIONS preflight requests
        if (org.springframework.http.HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        // 1. Skip validation for public endpoints
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        // 2. Extract Authorization Header
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.warn("Missing Authorization Header for path: {}", path);
            return handleUnauthorized(exchange, "Missing Authorization Header");
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid Authorization Header format for path: {}", path);
            return handleUnauthorized(exchange, "Invalid Authorization Header");
        }

        String token = authHeader.substring(7);

        // 3. Validate JWT Token
                // 3. Validate JWT Token
        try {
            Claims claims = parseToken(token);
            String username = claims.getSubject(); // Extract username from Subject
            String role = claims.get("role", String.class);

            log.info("Token validated successfully for user: {}, role: {}", username, role);

            // Add user info to request headers for downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", username)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            log.error("JWT Validation failed for path: {}", path, e);
            return handleUnauthorized(exchange, "Invalid or expired token");
        }

    }

    @Override
    public int getOrder() {
        return -1; // Execute before other filters
    }

    private boolean isPublicEndpoint(String path) {
        if (path.contains("/v3/api-docs") || path.contains("/swagger-ui") || path.contains("/webjars")) {
            return true;
        }
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(endpoint -> path.startsWith(endpoint));
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(io.jsonwebtoken.io.Decoders.BASE64.decode(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String jsonError = String.format(
                "{\"error\":\"Unauthorized\", \"message\":\"%s\"}",
                message);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(jsonError.getBytes())));
    }
}
