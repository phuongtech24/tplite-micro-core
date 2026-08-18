package com.tplite.banking.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    // Lấy Secret Key từ file cấu hình (application.yml)
    @Value("${jwt.secret}")
    private String secret;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Lấy toàn bộ thông tin (Claims) từ Token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Lấy tên User
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Lấy danh sách Quyền (Roles)
    public String extractRoles(String token) {
        List<?> roles = extractAllClaims(token).get("authorities", List.class);
        return roles != null ? roles.toString() : "";
    }

    // Kiểm tra Token còn hạn không
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Xác thực toàn diện Token
    public void validateToken(final String token) {
        // Hàm parseClaimsJws ở trên sẽ tự động ném Exception nếu:
        // 1. Chữ ký (Signature) bị sai (Token giả)
        // 2. Token đã hết hạn
        extractAllClaims(token);
    }
}
