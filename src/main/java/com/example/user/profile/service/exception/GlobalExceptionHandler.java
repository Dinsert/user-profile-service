package com.example.user.profile.service.exception;

import com.example.userprofile.api.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserProfileNotFound(UserProfileNotFoundException e, HttpServletRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null
                ? String.format("Mistake in field '%s': %s", fieldError.getField(), fieldError.getDefaultMessage())
                : "Error validation";
        log.warn("Error validation: {}", message);
        return createErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception e, HttpServletRequest request) {
        log.error("An unexpected error occurred: {}", e.getMessage());
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI());
    }

    private ResponseEntity<ErrorResponseDTO> createErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(OffsetDateTime.now(), status.value(), message, path);
        return ResponseEntity.status(status).body(errorResponse);
    }
}