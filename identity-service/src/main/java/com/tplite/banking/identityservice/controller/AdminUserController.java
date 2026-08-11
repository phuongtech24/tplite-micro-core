package com.tplite.banking.identityservice.controller;

import com.tplite.banking.common.dto.ApiResponse;
import com.tplite.banking.identityservice.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PutMapping("/{userId}/lock")
    public ApiResponse<String> lockUser(@PathVariable UUID userId) {
        String result = adminUserService.lockUser(userId);
        return ApiResponse.success(result);
    }

    @PutMapping("/{userId}/unlock")
    public ApiResponse<String> unlockUser(@PathVariable UUID userId) {
        String result = adminUserService.unlockUser(userId);
        return ApiResponse.success(result);
    }

    @PutMapping("/{userId}/reset-password")
    public ApiResponse<String> resetPassword(
            @PathVariable UUID userId, 
            @RequestParam String newPassword) {
        String result = adminUserService.resetPassword(userId, newPassword);
        return ApiResponse.success(result);
    }
}
