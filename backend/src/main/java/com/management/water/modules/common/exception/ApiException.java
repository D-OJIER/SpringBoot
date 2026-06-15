package com.management.water.modules.common.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

  private final HttpStatus status;
  private final String clientMessage;

  public ApiException(HttpStatus status, String clientMessage) {
    super(clientMessage);
    this.status = status;
    this.clientMessage = clientMessage;
  }

  public ApiException(HttpStatus status, String clientMessage, Throwable cause) {
    super(clientMessage, cause);
    this.status = status;
    this.clientMessage = clientMessage;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getClientMessage() {
    return clientMessage;
  }
}
