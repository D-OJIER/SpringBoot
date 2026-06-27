package com.management.water.security.interceptor;

import com.management.water.modules.common.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

  @Value("${api.key.header}")
  private String apiKeyHeader;

  @Value("${api.key.secret}")
  private String apiKeySecret;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String requestKey = request.getHeader(apiKeyHeader);

    // 1. If API Key is present in the request header, validate it
    if (requestKey != null) {
      if (requestKey.equals(apiKeySecret)) {
        return true;
      }
      throw new ApiException.UnauthorizedException("Invalid API Key");
    }

    // 2. Otherwise, check if request was already authenticated via JWT (populated in SecurityContext)
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken)) {
      return true;
    }

    throw new ApiException.UnauthorizedException("Authentication required (API Key or JWT)");
  }
}

