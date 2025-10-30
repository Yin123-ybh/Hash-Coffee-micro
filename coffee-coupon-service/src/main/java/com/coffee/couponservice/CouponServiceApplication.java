package com.coffee.couponservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 优惠券服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CouponServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CouponServiceApplication.class, args);
    }
}

