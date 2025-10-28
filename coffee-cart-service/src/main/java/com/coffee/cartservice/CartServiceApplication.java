package com.coffee.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 购物车服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CartServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }
}
