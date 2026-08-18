package com.tplite.banking.common.exception;

import com.tplite.banking.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Lỗi Validation (Khi người dùng truyền thiếu Data hoặc sai Format)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Validation error at {}: {}", request.getRequestURI(), errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", "Dữ liệu đầu vào không hợp lệ", request.getRequestURI(), errors));
    }

    // 2. Lỗi Logic Nghiệp vụ chuẩn (Do chúng ta chủ động quăng ra)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("Business error at {}: {}", request.getRequestURI(), errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.error(errorCode.getStatusCode(), errorCode.getCode(), errorCode.getMessage(), request.getRequestURI()));
    }

    // 3. Lỗi Gọi sai Method HTTP (Gọi POST thành GET)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String message = "HTTP method " + ex.getMethod() + " is not supported";
        log.warn("Method not supported at {}: {}", request.getRequestURI(), ex.getMethod());
        return buildError(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", message, request);
    }

    // 4. Lỗi IllegalArgumentException / IllegalStateException (Dùng sẵn của Java)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(RuntimeException ex, HttpServletRequest request) {
        log.warn("Bad logic request at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
    }

    // 5. Lỗi Đồng thời (Concurrency) - Optimistic Lock
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLocking(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        log.warn("Optimistic locking failure at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.CONFLICT, "CONCURRENCY_ERROR", "Dữ liệu đã bị người khác thay đổi, vui lòng tải lại trang (F5) và thử lại.", request);
    }

    // 6. Lỗi Hệ thống Không lường trước (Lỗi 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}", request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Lỗi hệ thống nội bộ, vui lòng thử lại sau", request);
    }

    private ResponseEntity<ApiResponse<Void>> buildError(HttpStatus status, String code, String message, HttpServletRequest request) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(status.value(), code, message, request.getRequestURI()));
    }
}
