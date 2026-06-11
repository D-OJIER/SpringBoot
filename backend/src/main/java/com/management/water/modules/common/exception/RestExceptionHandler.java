package com.management.water.modules.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("error", ex.getClientMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
    }
}
