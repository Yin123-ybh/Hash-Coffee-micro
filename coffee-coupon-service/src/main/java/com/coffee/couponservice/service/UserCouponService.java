package com.coffee.couponservice.service;

import com.coffee.couponservice.entity.UserCoupon;
import com.coffee.couponservice.vo.UserCouponVO;

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
    
    /**
     * 获取用户优惠券列表（包含优惠券详情）
     * @param userId 用户ID
     * @param status 状态（可选）
     * @return 用户优惠券VO列表
     */
    List<UserCouponVO> getUserCouponVOs(Long userId, Integer status);
    
    /**
     * 领取优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 领取结果
     */
    boolean claimCoupon(Long userId, Long couponId);
    
    /**
     * 使用优惠券
     * @param userCouponId 用户优惠券ID
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 使用结果
     */
    boolean useCoupon(Long userCouponId, Long userId, Long orderId);
}








