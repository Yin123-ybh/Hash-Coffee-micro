package com.coffee.couponservice.service;

import com.coffee.couponservice.entity.Coupon;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService {
    
    /**
     * 查询优惠券列表
     */
    List<Coupon> getCouponList(String name, Integer status, Integer type);
    
    /**
     * 统计优惠券总数
     */
    long countCoupons(String name, Integer status, Integer type);
    
    /**
     * 根据ID查询优惠券
     */
    Coupon getCouponById(Long id);
    
    /**
     * 更新优惠券使用数量
     */
    void updateCouponUsedCount(Long id, Integer usedCount);
}


