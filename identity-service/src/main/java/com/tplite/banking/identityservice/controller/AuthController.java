package com.tplite.banking.identityservice.controller;

import com.tplite.banking.identityservice.dto.AuthRequest;
import com.tplite.banking.identityservice.service.AuthService;
import com.tplite.banking.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody AuthRequest request) {
        String result = authService.register(request);
        return ApiResponse.success(result);
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody AuthRequest request) {
        String token = authService.login(request);
        return ApiResponse.success("Đăng nhập thành công", token);
    }

    @GetMapping("/me")
    public ApiResponse<String> getMyInfo() {
        // Lấy thông tin user đang đăng nhập từ SecurityContext (được lưu bởi Filter)
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String role = authentication.getAuthorities().toString();
        
        return ApiResponse.success("Xin chào " + username + "! Quyền của bạn là: " + role);
    }
}
