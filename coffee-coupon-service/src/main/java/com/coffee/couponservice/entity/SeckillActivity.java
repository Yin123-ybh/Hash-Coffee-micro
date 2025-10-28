package com.coffee.couponservice.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 秒杀活动实体类
 */
@Data
public class SeckillActivity {
    
    private Long id;
    
    private String name;
    
    private String description;
    
    private Long couponId;
    
    private Integer seckillStock;
    
    private Integer perUserLimit;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    // 关联的优惠券信息（用于前端显示）
    private String couponName;
    
    private Integer couponType;
}










