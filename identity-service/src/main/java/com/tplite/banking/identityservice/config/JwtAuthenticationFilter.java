package com.tplite.banking.identityservice.config;

import com.tplite.banking.identityservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    private static final String SECRET = "DayLaChieuKhoaBiMatSieuCapVipProCuaTPLiteBankingSystem";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lấy Token từ Header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        java.util.List<String> authoritiesList = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Cắt bỏ chữ "Bearer "
            try {
                // 2. Mở Token ra đọc (Giải mã)
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(SECRET.getBytes())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                
                username = claims.getSubject();
                authoritiesList = (java.util.List<String>) claims.get("authorities"); 
            } catch (Exception e) {
                System.out.println("Token giả mạo hoặc hết hạn!");
            }
        }

        // 3. Nếu Token chuẩn và chưa ai đăng nhập
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            java.util.List<SimpleGrantedAuthority> grantedAuthorities = java.util.Collections.emptyList();
            if (authoritiesList != null) {
                grantedAuthorities = authoritiesList.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(java.util.stream.Collectors.toList());
            }
            
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username, null, grantedAuthorities);
            
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // Ghi nhận: "User này đã xác thực thành công và có quyền"
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        // Cho đi tiếp vào các hàm bên trong
        filterChain.doFilter(request, response);
    }
}
