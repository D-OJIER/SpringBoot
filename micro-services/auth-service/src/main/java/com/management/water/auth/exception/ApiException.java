package com.management.water.auth.exception;

import org.springframework.http.HttpStatus;

/**
 * Base API exception — local copy inside auth-service.
 * Each microservice owns its own copy of this class (no shared library yet).
 */
public class ApiException extends RuntimeException {

  private final HttpStatus status;
  private final String clientMessage;

  public ApiException(HttpStatus status, String clientMessage) {
    super(clientMessage);
    this.status = status;
    this.clientMessage = clientMessage;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getClientMessage() {
    return clientMessage;
  }

  // --- Convenience subclasses ---

  public static class NotFoundException extends ApiException {
    public NotFoundException(String message) {
      super(HttpStatus.NOT_FOUND, message);
    }
  }

  public static class ConflictException extends ApiException {
    public ConflictException(String message) {
      super(HttpStatus.CONFLICT, message);
    }
  }

  public static class BadRequestException extends ApiException {
    public BadRequestException(String message) {
      super(HttpStatus.BAD_REQUEST, message);
    }
  }

  public static class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
      super(HttpStatus.UNAUTHORIZED, message);
    }
  }
}
