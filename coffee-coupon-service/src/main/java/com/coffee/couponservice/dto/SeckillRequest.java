package com.coffee.couponservice.dto;

import lombok.Data;

/**
 * 秒杀请求DTO
 */
@Data
public class SeckillRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 秒杀数量
     */
    private Integer quantity;
}

