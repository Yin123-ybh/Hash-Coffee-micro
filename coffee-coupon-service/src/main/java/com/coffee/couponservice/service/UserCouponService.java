package com.coffee.couponservice.service;

import com.coffee.couponservice.entity.UserCoupon;

import java.util.List;

/**
 * 用户优惠券服务接口
 */
public interface UserCouponService {
    
    /**
     * 获取用户优惠券列表
     * @param userId 用户ID
     * @param status 状态（可选）
     * @return 用户优惠券列表
     */
    List<UserCoupon> getUserCoupons(Long userId, Integer status);
}
