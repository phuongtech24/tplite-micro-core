package com.tplite.banking.common.exception;

import com.tplite.banking.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Bad request at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Logic error at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "LOGIC_ERROR", ex.getMessage(), request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        log.warn("Runtime error at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "RUNTIME_ERROR", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}", request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Lỗi hệ thống nội bộ", request);
    }

    private ResponseEntity<ApiResponse<Void>> buildError(HttpStatus status, String code, String message, HttpServletRequest request) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(status.value(), code, message, request.getRequestURI()));
    }
}
