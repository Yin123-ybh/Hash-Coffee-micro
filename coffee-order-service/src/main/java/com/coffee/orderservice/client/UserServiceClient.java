package com.coffee.orderservice.client;

import com.coffee.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "coffee-user-service")
public interface UserServiceClient {
    
    /**
     * 获取用户信息
     */
    @GetMapping("/user/info")
    Result<Object> getUserInfo();
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/user/{id}")
    Result<Object> getUserById(@PathVariable("id") Long id);
}

