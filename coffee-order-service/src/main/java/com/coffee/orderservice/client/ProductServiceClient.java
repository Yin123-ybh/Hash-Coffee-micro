package com.coffee.orderservice.client;

import com.coffee.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品服务Feign客户端
 */
@FeignClient(name = "coffee-product-service")
public interface ProductServiceClient {
    
    /**
     * 根据ID获取商品详情
     */
    @GetMapping("/product/{id}")
    Result<Object> getProductById(@PathVariable("id") Long id);
    
    /**
     * 获取商品列表
     */
    @GetMapping("/product/list")
    Result<Object> getProductList();
}

