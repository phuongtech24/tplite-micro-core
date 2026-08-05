package com.tplite.banking.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 500
    UNCATEGORIZED_EXCEPTION(500, "SYS_999", "Lỗi hệ thống không xác định"),
    
    // Auth Errors (400, 404, 401...)
    USER_EXISTED(400, "AUTH_001", "Tên đăng nhập đã tồn tại"),
    USER_NOT_FOUND(404, "AUTH_002", "Không tìm thấy người dùng"),
    INVALID_PASSWORD(400, "AUTH_003", "Sai mật khẩu"),
    UNAUTHORIZED(401, "AUTH_004", "Không có quyền truy cập"),
    
    // Account Errors
    INSUFFICIENT_BALANCE(400, "ACC_001", "Số dư khả dụng không đủ"),
    ACCOUNT_NOT_FOUND(404, "ACC_002", "Không tìm thấy tài khoản"),
    INVALID_AMOUNT(400, "ACC_003", "Số tiền không hợp lệ (phải lớn hơn 0)"),
    
    // Transfer Errors
    TRANSFER_FAILED(400, "TXN_001", "Giao dịch thất bại"),
    DUPLICATE_IDEMPOTENCY(400, "TXN_002", "Giao dịch này đã được ghi nhận trước đó"),
    ACCOUNT_SERVICE_UNAVAILABLE(503, "TXN_003", "Dịch vụ Tài khoản tạm thời không khả dụng");

    private final int statusCode;
    private final String code;
    private final String message;
}
