package com.tplite.banking.identityservice.service.impl;

import com.tplite.banking.identityservice.dto.AuthRequest;
import com.tplite.banking.identityservice.entity.*;
import com.tplite.banking.identityservice.repository.*;
import com.tplite.banking.identityservice.service.AuthService;
import com.tplite.banking.identityservice.enums.RoleName;
import com.tplite.banking.common.exception.BusinessException;
import com.tplite.banking.common.exception.ErrorCode;
import com.tplite.banking.identityservice.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.tplite.banking.identityservice.dto.EkycVerifyRequest;
import com.tplite.banking.identityservice.enums.EkycStatus;
import com.tplite.banking.identityservice.service.EkycService;
import com.tplite.banking.identityservice.client.AccountClient;
import com.tplite.banking.common.dto.ApiResponse;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private EkycService ekycService;
    
    @Autowired
    private AccountClient accountClient;

    @Transactional
    public String register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(ErrorCode.USER_EXISTED);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); 
        user = userRepository.save(user);

        // Gán Role mặc định là CUSTOMER
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(RoleName.CUSTOMER);
                    return roleRepository.save(r);
                });

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(customerRole);
        userRoleRepository.save(userRole);

        return "Đăng ký thành công!";
    }

    public String login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 1. Lấy tất cả Roles của User
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        
        List<String> authorities = new ArrayList<>();
        List<Long> roleIds = new ArrayList<>();

        for (UserRole ur : userRoles) {
            authorities.add("ROLE_" + ur.getRole().getName().name());
            roleIds.add(ur.getRole().getId());
        }

        // 2. Lấy tất cả Permissions của các Roles đó
        if (!roleIds.isEmpty()) {
            List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIdIn(roleIds);
            for (RolePermission rp : rolePermissions) {
                authorities.add(rp.getPermission().getName());
            }
        }

        // 3. Cấp Token chứa toàn bộ Quyền
        return jwtService.generateToken(user.getUsername(), authorities);
    }

    @Transactional
    public String verifyEkyc(EkycVerifyRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getEkycStatus() == EkycStatus.VERIFIED) {
            throw new RuntimeException("Tài khoản đã được xác thực eKYC!");
        }

        boolean isMatched = ekycService.verifyIdentity(request.getIdCardUrl(), request.getSelfieUrl());
        if (!isMatched) {
            user.setEkycStatus(EkycStatus.FAILED);
            userRepository.save(user);
            throw new RuntimeException("Xác thực eKYC thất bại (Nghi ngờ giả mạo)!");
        }

        // Cập nhật thông tin eKYC
        user.setFullName(request.getFullName());
        user.setIdCardNumber(request.getIdCardNumber());
        user.setEkycStatus(EkycStatus.VERIFIED);
        userRepository.save(user);

        // Gọi sang Account Service để tạo Số tài khoản TPBank
        ApiResponse<String> response = accountClient.createAccount(user.getId());
        if (response.isSuccess()) {
            return "Xác thực eKYC thành công! Số tài khoản TPBank của bạn là: " + response.getData();
        } else {
            throw new RuntimeException("Lỗi khi mở tài khoản ngân hàng!");
        }
    }
}
