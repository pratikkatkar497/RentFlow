
package com.RentFlow.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.RentFlow.response.ApiResponse;
import com.RentFlow.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================
    // 403 - Access Denied
    // =========================================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex) {

        ApiResponse<Object> response = new ApiResponse<>();

        response.setSuccess(false);
        response.setMessage(
                ex.getMessage() != null
                        ? ex.getMessage()
                        : "Access denied");
        response.setData(null);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    // =========================================================
    // 409 - Email Already Exists
    // =========================================================

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {

        return buildErrorResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT);
    }

    // =========================================================
    // 409 - Phone Already Exists
    // =========================================================

    @ExceptionHandler(PhoneAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePhoneAlreadyExists(
            PhoneAlreadyExistsException ex) {

        return buildErrorResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT);
    }

    // =========================================================
    // 404 - Resource Not Found
    // =========================================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return buildErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND);
    }

    // =========================================================
    // 400 - Bad Request
    // =========================================================

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex) {

        return buildErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    // =========================================================
    // 401 - Invalid Credentials
    // =========================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex) {

        return buildErrorResponse(
                "Invalid email or password",
                HttpStatus.UNAUTHORIZED);
    }

    // =========================================================
    // 400 - Validation Error
    // =========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        String message = "Invalid request";

        if (ex.getBindingResult().getFieldError() != null) {

            message = ex.getBindingResult()
                    .getFieldError()
                    .getDefaultMessage();
        }

        return buildErrorResponse(
                message,
                HttpStatus.BAD_REQUEST);
    }

    // =========================================================
    // 500 - Unexpected Error
    // =========================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {

        /*
         * Do NOT expose ex.getMessage() to the client.
         * Internal exception details should remain on the server.
         */

        return buildErrorResponse(
                "An unexpected error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // =========================================================
    // Common Error Response Builder
    // =========================================================

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            HttpStatus status) {

        ErrorResponse error = new ErrorResponse(
                false,
                message,
                status.value(),
                LocalDateTime.now());

        return ResponseEntity
                .status(status)
                .body(error);
    }
}

