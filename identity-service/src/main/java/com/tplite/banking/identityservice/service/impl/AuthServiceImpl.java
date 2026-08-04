package com.tplite.banking.identityservice.service.impl;

import com.tplite.banking.identityservice.dto.AuthRequest;
import com.tplite.banking.identityservice.entity.*;
import com.tplite.banking.identityservice.repository.*;
import com.tplite.banking.identityservice.service.AuthService;
import com.tplite.banking.identityservice.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public String register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); 
        user = userRepository.save(user);

        // Gán Role mặc định là CUSTOMER
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("CUSTOMER");
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Sai mật khẩu rồi bạn ơi!");
        }

        // 1. Lấy tất cả Roles của User
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        
        List<String> authorities = new ArrayList<>();
        List<Long> roleIds = new ArrayList<>();

        for (UserRole ur : userRoles) {
            authorities.add("ROLE_" + ur.getRole().getName());
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
}
