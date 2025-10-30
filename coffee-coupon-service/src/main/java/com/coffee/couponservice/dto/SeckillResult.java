package com.coffee.couponservice.dto;

import lombok.Data;

/**
 * 秒杀结果DTO
 */
@Data
public class SeckillResult {
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 订单号（如果成功）
     */
    private String orderNo;
}

