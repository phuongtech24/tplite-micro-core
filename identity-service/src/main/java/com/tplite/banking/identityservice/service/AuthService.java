package com.tplite.banking.identityservice.service;

import com.tplite.banking.identityservice.dto.AuthRequest;
import com.tplite.banking.identityservice.entity.User;
import com.tplite.banking.identityservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;

    public String register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        // Mã hóa mật khẩu
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); 
        
        userRepository.save(user);
        return "Đăng ký thành công!";
    }

    public String login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Sai mật khẩu rồi bạn ơi!");
        }

        // Cấp Token
        return jwtService.generateToken(user.getUsername());
    }
}
