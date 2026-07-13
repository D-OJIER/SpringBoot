package com.management.water.gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class JwtGlobalFilterTest {

    @InjectMocks
    private JwtGlobalFilter jwtGlobalFilter;

    @Mock
    private GatewayFilterChain chain;

    private final String rawSecret = "849864a7cc912ad511ed4f19569c1dc3e1b4519bb81059882cea44f51787c7aa";
    private String validToken;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtGlobalFilter, "secret", rawSecret);

        byte[] keyBytes = Base64.getDecoder().decode(rawSecret);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        validToken = Jwts.builder()
                .setSubject("adminuser")
                .claim("role", "ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    public void testFilter_PublicEndpoint_Bypass() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        jwtGlobalFilter.filter(exchange, chain);

        verify(chain, times(1)).filter(exchange);
    }

    @Test
    public void testFilter_MissingAuthorizationHeader_Unauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/apartments").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        jwtGlobalFilter.filter(exchange, chain);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }

    @Test
    public void testFilter_InvalidAuthorizationHeaderFormat_Unauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/apartments")
                .header(HttpHeaders.AUTHORIZATION, "InvalidFormat " + validToken)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        jwtGlobalFilter.filter(exchange, chain);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }

    @Test
    public void testFilter_InvalidToken_Unauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/apartments")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-signature-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        jwtGlobalFilter.filter(exchange, chain);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }

    @Test
    public void testFilter_ValidToken_SuccessAndHeaderPropagation() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/apartments")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        ArgumentCaptor<ServerWebExchange> exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);

        jwtGlobalFilter.filter(exchange, chain);

        verify(chain, times(1)).filter(exchangeCaptor.capture());
        ServerWebExchange capturedExchange = exchangeCaptor.getValue();
        assertNotNull(capturedExchange);

        assertEquals("adminuser", capturedExchange.getRequest().getHeaders().getFirst("X-User-Id"));
        assertEquals("ADMIN", capturedExchange.getRequest().getHeaders().getFirst("X-User-Role"));
    }
}
