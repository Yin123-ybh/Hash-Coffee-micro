package com.coffee.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.coffee.common.utils.JwtUtils;
import com.coffee.gateway.config.GatewayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 认证全局过滤器
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    
    private static final Logger log = LoggerFactory.getLogger(AuthGlobalFilter.class);
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().toString();
        
        // 跳过OPTIONS预检请求
        if ("OPTIONS".equals(method)) {
            return chain.filter(exchange);
        }
        
        // 跳过不需要认证的路径
        if (isSkipAuth(path)) {
            return chain.filter(exchange);
        }
        
        // 获取Authorization头
        String authorization = request.getHeaders().getFirst("Authorization");
        ServerHttpRequest newRequest = request;
        
        // 如果请求头中有Authorization，尝试提取用户信息
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            if (StringUtils.hasText(token) && JwtUtils.validateToken(token)) {
                // 从JWT中提取用户信息并传递给下游服务
                String userId = JwtUtils.getUserIdFromToken(token);
                String username = JwtUtils.getUsernameFromToken(token);
                
                // 将用户信息添加到请求头中，传递给下游服务
                newRequest = request.mutate()
                        .header("X-User-Id", userId != null ? userId : "")
                        .header("X-User-Name", username != null ? username : "")
                        .build();
                
                log.info("请求路径 {} 用户信息已提取, userId: {}, username: {}", path, userId, username);
            }
        }
        
        // TODO: 临时跳过订单接口的认证，用于测试
        if (path.startsWith("/order/")) {
            log.warn("临时跳过订单接口认证: {}", path);
            return chain.filter(exchange.mutate().request(newRequest).build());
        }
        
        // 对于其他路径，如果没有认证信息，返回401
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            log.warn("请求路径 {} 缺少认证信息", path);
            return GatewayConfig.handleError(exchange, "未授权访问");
        }
        
        // 验证JWT是否有效
        String token = authorization.substring(7);
        if (!StringUtils.hasText(token)) {
            log.warn("请求路径 {} 令牌为空", path);
            return GatewayConfig.handleError(exchange, "令牌无效");
        }
        
        // 验证JWT是否有效
        if (!JwtUtils.validateToken(token)) {
            log.warn("请求路径 {} JWT无效或已过期", path);
            return GatewayConfig.handleError(exchange, "令牌无效或已过期");
        }
        
        log.info("请求路径 {} 认证通过", path);
        return chain.filter(exchange.mutate().request(newRequest).build());
    }
    
    /**
     * 判断是否需要跳过认证
     */
    private boolean isSkipAuth(String path) {
        // 登录、注册等公开接口
        String[] skipPaths = {
                "/user/login",
                "/user/register",
                "/admin/login",  // 管理端登录
                "/product/list",
                "/product/recommended",
                "/product/hot",
                "/product/"
        };
        
        for (String skipPath : skipPaths) {
            if (path.startsWith(skipPath)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public int getOrder() {
        return -100; // 优先级最高
    }
}
