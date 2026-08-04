package com.tplite.banking.identityservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET = "DayLaChieuKhoaBiMatSieuCapVipProCuaTPLiteBankingSystem";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    private Key cachedKey;

    // Giai đoạn 3: Sẵn sàng (Initialization)
    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println(">>> [VÒNG ĐỜI BEAN] JwtService đang khởi tạo: Đang băm Secret Key 1 lần duy nhất...");
        this.cachedKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Giai đoạn 4: Phá hủy (Destruction)
    @jakarta.annotation.PreDestroy
    public void destroy() {
        System.out.println(">>> [VÒNG ĐỜI BEAN] JwtService sắp bị tiêu diệt: Xóa Key khỏi RAM để bảo mật...");
        this.cachedKey = null;
    }

    private Key getSignKey() {
        return this.cachedKey; // Tái sử dụng Key đã tạo từ lúc PostConstruct
    }

    public String generateToken(String username, java.util.List<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorities); // Lưu toàn bộ Role và Permission vào Token

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username) // Người sở hữu Token này là ai?
                .setIssuedAt(new Date(System.currentTimeMillis())) // Ngày cấp
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Ngày hết hạn
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Ký bằng chìa khóa và thuật toán HS256
                .compact(); // Đóng gói lại thành 1 chuỗi String
    }
}
