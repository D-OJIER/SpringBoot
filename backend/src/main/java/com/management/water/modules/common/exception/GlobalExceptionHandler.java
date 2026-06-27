package com.management.water.modules.common.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // 1. Custom/Subclassed API Exceptions (4xx, 5xx)
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
    HttpStatus status = ex.getStatus();
    return ResponseEntity.status(status)
        .body(new ErrorResponse(ex.getClientMessage(), status.value(), LocalDateTime.now()));
  }

  // 2. Bean/Payload Validation Errors (400)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining(", "));
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(new ErrorResponse(message, status.value(), LocalDateTime.now()));
  }

  // 3. Path Variables & Request Param Constraints Validation (e.g. @Min(1) @PathVariable) (400)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
    String message = ex.getConstraintViolations().stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .collect(Collectors.joining(", "));
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(new ErrorResponse(message, status.value(), LocalDateTime.now()));
  }

  // 4. Missing Required Parameters (e.g. absent @RequestParam) (400)
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
    String message = String.format("Required parameter '%s' is missing", ex.getParameterName());
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(new ErrorResponse(message, status.value(), LocalDateTime.now()));
  }

  // 5. Invalid URL Parameter Type Mismatches (e.g. String passed to Long) (400)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String message = String.format("Invalid value for parameter '%s'", ex.getName());
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(new ErrorResponse(message, status.value(), LocalDateTime.now()));
  }

  // 6. Malformed JSON Body (400)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(new ErrorResponse("Invalid request body or malformed JSON", status.value(), LocalDateTime.now()));
  }

  // 7. Database Constraint Violations (Duplicate/FK violations) (409)
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
    HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity.status(status)
        .body(new ErrorResponse("Database conflict or constraint violation", status.value(), LocalDateTime.now()));
  }

  // 8. Unsupported HTTP Method (405)
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    String message = String.format("HTTP Method '%s' is not supported for this endpoint", ex.getMethod());
    HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
    return ResponseEntity.status(status)
        .body(new ErrorResponse(message, status.value(), LocalDateTime.now()));
  }

  // 9. Method Level Security Access Denied (@PreAuthorize check failures) (403)
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
    HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status)
        .body(new ErrorResponse("Access denied", status.value(), LocalDateTime.now()));
  }

  // 10. Fallback Standard Java Argument Exceptions (400)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
        .body(new ErrorResponse(ex.getMessage(), status.value(), LocalDateTime.now()));
  }

  // 11. Generic Global Fallback (500)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Unhandled exception occurred on the server", ex);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return ResponseEntity.status(status)
        .body(new ErrorResponse("Internal server error", status.value(), LocalDateTime.now()));
  }
}

