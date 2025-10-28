package com.coffee.couponservice.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PublicCouponVO {
    private Long id;
    private String title;                 // 展示标题（=name）
    private String name;                  // 兼容字段
    private BigDecimal discountAmount;    // 面额
    private BigDecimal amount;            // 兼容字段
    private BigDecimal threshold;         // 门槛
    private BigDecimal minAmount;         // 兼容字段
    private Integer type;
    private LocalDateTime startTime;
    private LocalDateTime expireTime;     // = endTime
    private LocalDateTime endTime;        // 兼容字段
    private Integer total;                // 总量
    private Integer used;                 // 已用
    private Integer stock;                // 剩余（通用）
    private Integer remain;               // 兼容前端字段
    private Integer remainCount;          // 兼容前端字段
    private Integer left;                 // 兼容前端字段
    private Integer leftCount;            // 兼容前端字段
    private Integer status;
}
