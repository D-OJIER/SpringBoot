package com.management.water.waterconfig.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    public void testApiExceptionSubclasses() {
        ApiException.NotFoundException notFound = new ApiException.NotFoundException("Not found message");
        assertEquals(HttpStatus.NOT_FOUND, notFound.getStatus());
        assertEquals("Not found message", notFound.getClientMessage());

        ApiException.ConflictException conflict = new ApiException.ConflictException("Conflict message");
        assertEquals(HttpStatus.CONFLICT, conflict.getStatus());
        assertEquals("Conflict message", conflict.getClientMessage());

        ApiException.BadRequestException badRequest = new ApiException.BadRequestException("Bad request message");
        assertEquals(HttpStatus.BAD_REQUEST, badRequest.getStatus());
        assertEquals("Bad request message", badRequest.getClientMessage());

        ApiException.UnauthorizedException unauthorized = new ApiException.UnauthorizedException("Unauthorized message");
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorized.getStatus());
        assertEquals("Unauthorized message", unauthorized.getClientMessage());
    }

    @Test
    public void testErrorResponseGettersAndSetters() {
        LocalDateTime time = LocalDateTime.now();
        ErrorResponse response = new ErrorResponse();
        response.setMessage("test message");
        response.setStatus(200);
        response.setTimestamp(time);

        assertEquals("test message", response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals(time, response.getTimestamp());

        ErrorResponse response2 = new ErrorResponse("message2", 400, time);
        assertEquals("message2", response2.getMessage());
        assertEquals(400, response2.getStatus());
        assertEquals(time, response2.getTimestamp());
    }

    @Test
    public void testHandleApiException() {
        ApiException ex = new ApiException(HttpStatus.NOT_ACCEPTABLE, "Client message");
        ResponseEntity<ErrorResponse> response = handler.handleApiException(ex);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Client message", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), response.getBody().getStatus());
    }

    @Test
    public void testHandleMethodArgumentNotValid() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("user", "username", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("username: must not be blank", response.getBody().getMessage());
    }

    @Test
    public void testHandleConstraintViolation() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("username");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must contain letters");
        when(ex.getConstraintViolations()).thenReturn(Collections.singleton(violation));

        ResponseEntity<ErrorResponse> response = handler.handleConstraint(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("username: must contain letters", response.getBody().getMessage());
    }

    @Test
    public void testHandleMissingServletRequestParameter() {
        MissingServletRequestParameterException ex = 
            new MissingServletRequestParameterException("id", "Long");
        ResponseEntity<ErrorResponse> response = handler.handleMissingParam(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Required parameter 'id' is missing", response.getBody().getMessage());
    }

    @Test
    public void testHandleMethodArgumentTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");
        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid value for parameter 'id'", response.getBody().getMessage());
    }

    @Test
    public void testHandleHttpMessageNotReadable() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        ResponseEntity<ErrorResponse> response = handler.handleMalformedJson(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid request body or malformed JSON", response.getBody().getMessage());
    }

    @Test
    public void testHandleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("data issue");
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Database conflict or constraint violation", response.getBody().getMessage());
    }

    @Test
    public void testHandleHttpRequestMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("PUT");
        ResponseEntity<ErrorResponse> response = handler.handleMethodNotSupported(ex);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("HTTP Method 'PUT' is not supported for this endpoint", response.getBody().getMessage());
    }

    @Test
    public void testHandleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("no entry");
        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Access denied", response.getBody().getMessage());
    }

    @Test
    public void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("bad argument");
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("bad argument", response.getBody().getMessage());
    }

    @Test
    public void testHandleGenericException() {
        Exception ex = new Exception("unhandled error");
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().getMessage());
    }
}
