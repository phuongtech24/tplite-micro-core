package com.tplite.banking.apigateway.filter;

import com.tplite.banking.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 1. Cho phép đi qua nếu là API Đăng nhập/Đăng ký
        if (request.getURI().getPath().contains("/api/v1/auth")) {
            return chain.filter(exchange);
        }

        // 2. Kiểm tra xem có đưa "Vé" (Header Authorization) không?
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); // Đuổi về
        }

        // 3. Lấy cái vé ra xem
        String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7); // Bỏ chữ "Bearer " đi
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 4. Máy soi vé (Validate Token)
        try {
            jwtUtil.validateToken(authHeader);
            
            // Lấy danh sách Quyền (Roles)
            String roles = jwtUtil.extractRoles(authHeader);

            // Tùy chọn: Lấy Username và Roles nhét vào Header mới để các Service bên trong khỏi phải giải mã lại
            String username = jwtUtil.extractUsername(authHeader);
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Name", username)
                    .header("X-User-Roles", roles)
                    .build();

            // KIỂM TRA QUYỀN (RBAC - TRAM KIỂM SOÁT TẠI CỔNG)
            String path = request.getURI().getPath();
            if (path.contains("/trial-balance") || path.contains("/api/v1/admin")) {
                if (!roles.contains("ROLE_ADMIN")) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN); // 403 Forbidden
                    return exchange.getResponse().setComplete();
                }
            }
            
            // Cho qua trạm thu phí
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            // Vé giả hoặc hết hạn!
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    // Đặt độ ưu tiên của Filter này lên hàng đầu
    @Override
    public int getOrder() {
        return -1; 
    }
}
