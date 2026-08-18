package com.tplite.banking.identityservice.service.impl;

import com.tplite.banking.common.exception.BusinessException;
import com.tplite.banking.common.exception.ErrorCode;
import com.tplite.banking.identityservice.entity.User;
import com.tplite.banking.identityservice.enums.UserStatus;
import com.tplite.banking.identityservice.repository.UserRepository;
import com.tplite.banking.identityservice.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String lockUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.LOCKED) {
            return "Tài khoản này đã bị khóa từ trước.";
        }

        user.setStatus(UserStatus.LOCKED);
        userRepository.save(user);
        return "Đã khóa tài khoản thành công!";
    }

    @Override
    @Transactional
    public String unlockUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.ACTIVE) {
            return "Tài khoản này đang hoạt động bình thường.";
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        return "Đã mở khóa tài khoản thành công!";
    }

    @Override
    @Transactional
    public String resetPassword(UUID userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Đã đặt lại mật khẩu thành công!";
    }
}
