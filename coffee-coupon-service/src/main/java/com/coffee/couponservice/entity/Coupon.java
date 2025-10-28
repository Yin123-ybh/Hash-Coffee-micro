package com.coffee.couponservice.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 */
@Data
public class Coupon {
    
    /**
     * 优惠券ID
     */
    private Long id;
    
    /**
     * 优惠券名称
     */
    private String name;
    
    /**
     * 优惠券类型 1-满减券 2-折扣券 3-代金券
     */
    private Integer type;
    
    /**
     * 优惠值
     */
    private Double discountValue;
    
    /**
     * 最低消费金额
     */
    private Double minAmount;
    
    /**
     * 发放总数
     */
    private Integer totalCount;
    
    /**
     * 已使用数量
     */
    private Integer usedCount;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
