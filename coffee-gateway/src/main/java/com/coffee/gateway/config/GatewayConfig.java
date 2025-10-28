package com.coffee.gateway.config;

import com.alibaba.fastjson.JSON;
import com.coffee.gateway.filter.AuthGlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 网关配置类
 */
@Configuration
public class GatewayConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 地址路由 - 需要优先匹配
                .route("address-service", r -> r.path("/address/**")
                        .uri("lb://coffee-user-service"))
                
                // 用户优惠券路由 - 需要优先匹配
                .route("user-coupon-service", r -> r.path("/user/coupons/**")
                        .filters(f -> f.rewritePath("/user/coupons(.*)", "/user/coupons$1"))
                        .uri("lb://coffee-coupon-service"))
                
                // 用户服务路由 - 小程序端（不需要stripPrefix，因为controller已经有@RequestMapping("/user")）
                .route("user-service", r -> r.path("/user/**")
                        .uri("lb://coffee-user-service"))
                
                // 商品服务路由 - 小程序端（不需要stripPrefix，因为controller已经有@RequestMapping("/product")）
                .route("product-service", r -> r.path("/product/**")
                        .uri("lb://coffee-product-service"))
                
                // 订单服务路由 - 小程序端（不需要stripPrefix，因为controller已经有@RequestMapping("/order")）
                .route("order-service", r -> r.path("/order/**")
                        .uri("lb://coffee-order-service"))
                
                // 优惠券服务路由
                .route("coupon-service", r -> r.path("/coupon/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://coffee-coupon-service"))
                
                // 购物车服务路由
                .route("cart-service", r -> r.path("/cart/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://coffee-cart-service"))
                
                // AI服务路由
                .route("ai-service", r -> r.path("/ai/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://coffee-ai-service"))
                
                // 统计服务路由
                .route("statistics-service", r -> r.path("/statistics/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://coffee-statistics-service"))
                
                // 管理端路由 - 商品管理
                .route("admin-product-service", r -> r.path("/admin/products/**")
                        .filters(f -> f.rewritePath("/admin/products(.*)", "/product/list$1"))
                        .uri("lb://coffee-product-service"))
                
                // 管理端路由 - 订单管理
                .route("admin-order-service", r -> r.path("/admin/orders/**")
                        .filters(f -> f.rewritePath("/admin/orders(.*)", "/order/list$1"))
                        .uri("lb://coffee-order-service"))
                
                // 管理端路由 - 用户管理
                .route("admin-user-service", r -> r.path("/admin/users/**")
                        .filters(f -> f.rewritePath("/admin/users(.*)", "/user/list$1"))
                        .uri("lb://coffee-user-service"))
                
                // 管理端路由 - 数据统计
                .route("admin-statistics-service", r -> r.path("/admin/statistics/**")
                        .filters(f -> f.rewritePath("/admin/statistics(.*)", "/statistics$1"))
                        .uri("lb://coffee-statistics-service"))
                
                // 管理端路由 - 优惠券管理
                .route("admin-coupon-service", r -> r.path("/admin/coupons/**")
                        .filters(f -> f.rewritePath("/admin/coupons(.*)", "/coupon$1"))
                        .uri("lb://coffee-coupon-service"))
                
                // 管理端路由 - 秒杀管理
                .route("admin-seckill-service", r -> r.path("/admin/coupon-seckill/**")
                        .filters(f -> f.rewritePath("/admin/coupon-seckill(.*)", "/coupon/seckill$1"))
                        .uri("lb://coffee-coupon-service"))
                
                .build();
    }
    
    // 移除重复的Bean定义，AuthGlobalFilter已经使用@Component注解
    
    /**
     * 统一错误处理
     */
    public static Mono<Void> handleError(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("data", null);
        
        String body = JSON.toJSONString(result);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
