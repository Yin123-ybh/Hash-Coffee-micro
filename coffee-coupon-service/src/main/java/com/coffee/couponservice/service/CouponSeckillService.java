package com.coffee.couponservice.service;

import com.coffee.common.result.Result;
import com.coffee.couponservice.dto.SeckillRequest;
import com.coffee.couponservice.dto.SeckillResult;

/**
 * 优惠券秒杀服务接口
 */
public interface CouponSeckillService {
    
    /**
     * 秒杀优惠券（使用Redis + Lua脚本防超卖）
     */
    Result<SeckillResult> seckillCoupon(SeckillRequest request);
    
    /**
     * 初始化秒杀库存到Redis
     */
    void initSeckillStock(Long couponId, Integer stock);
    
    /**
     * 获取秒杀库存
     */
    Integer getSeckillStock(Long couponId);
    
    /**
     * 检查用户是否已参与秒杀
     */
    boolean checkUserSeckill(Long userId, Long couponId);
}
