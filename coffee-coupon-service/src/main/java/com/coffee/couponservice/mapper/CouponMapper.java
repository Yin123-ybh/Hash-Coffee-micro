package com.coffee.couponservice.mapper;

import com.coffee.couponservice.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠券Mapper接口
 */
@Mapper
public interface CouponMapper {
    
    /**
     * 根据ID查询优惠券
     */
    Coupon selectById(@Param("id") Long id);
    
    /**
     * 查询所有可用的优惠券
     */
    List<Coupon> selectAvailableCoupons();
    
    /**
     * 更新优惠券使用数量
     */
    int updateUsedCount(@Param("id") Long id, @Param("usedCount") Integer usedCount);
    
    /**
     * 插入优惠券
     */
    int insert(Coupon coupon);
    
    /**
     * 更新优惠券
     */
    int update(Coupon coupon);
    
    /**
     * 删除优惠券
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 分页查询优惠券（管理端）
     */
    List<Coupon> selectCouponList(
            @Param("name") String name,
            @Param("status") Integer status,
            @Param("type") Integer type);
    
    /**
     * 统计优惠券总数（管理端）
     */
    long countCoupons(
            @Param("name") String name,
            @Param("status") Integer status,
            @Param("type") Integer type);
}
