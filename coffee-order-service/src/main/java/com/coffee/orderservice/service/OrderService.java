package com.coffee.orderservice.service;

import java.util.Map;

/**
 * 订单服务接口
 * 职责：定义订单业务逻辑方法
 */
public interface OrderService {
    
    /**
     * 创建订单
     * @param orderData 订单数据
     * @param userId 用户ID
     * @return 订单信息
     * @throws IllegalArgumentException 参数错误
     */
    Map<String, Object> createOrder(Map<String, Object> orderData, Long userId) throws IllegalArgumentException;
}

