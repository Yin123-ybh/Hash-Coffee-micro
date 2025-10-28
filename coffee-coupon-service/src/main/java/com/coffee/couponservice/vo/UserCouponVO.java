package com.coffee.couponservice.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券VO
 */
@Data
public class UserCouponVO {
    
    /**
     * 用户优惠券ID
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
     * 优惠券名称
     */
    private String name;
    
    /**
     * 优惠券标题
     */
    private String title;
    
    /**
     * 优惠券类型 1-满减券 2-折扣券
     */
    private Integer type;
    
    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 最低消费金额
     */
    private BigDecimal minAmount;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 状态 1-未使用 2-已使用 3-已过期
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
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
