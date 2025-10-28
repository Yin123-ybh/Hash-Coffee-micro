package com.coffee.couponservice.mapper;

import com.coffee.couponservice.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户优惠券Mapper接口
 */
@Mapper
public interface UserCouponMapper {
    
    /**
     * 根据ID查询用户优惠券
     */
    UserCoupon selectById(@Param("id") Long id);
    
    /**
     * 根据用户ID和优惠券ID查询
     */
    UserCoupon selectByUserIdAndCouponId(@Param("userId") Long userId, @Param("couponId") Long couponId);
    
    /**
     * 根据用户ID查询用户优惠券列表
     */
    List<UserCoupon> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 插入用户优惠券
     */
    int insert(UserCoupon userCoupon);
    
    /**
     * 更新用户优惠券状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("orderId") Long orderId);
    
    /**
     * 根据用户ID和优惠券ID删除
     */
    int deleteByUserIdAndCouponId(@Param("userId") Long userId, @Param("couponId") Long couponId);
}
