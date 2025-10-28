package com.coffee.couponservice.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 */
@Data
public class UserCoupon {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 状态 0-未使用 1-已使用 2-已过期
     */
    private Integer status;
    
    /**
     * 使用时间
     */
    private LocalDateTime usedTime;
    
    /**
     * 使用订单ID
     */
    private Long orderId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
