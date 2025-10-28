package com.coffee.couponservice.service.impl;

import com.coffee.couponservice.entity.UserCoupon;
import com.coffee.couponservice.mapper.UserCouponMapper;
import com.coffee.couponservice.service.UserCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户优惠券服务实现类
 */
@Slf4j
@Service
public class UserCouponServiceImpl implements UserCouponService {
    
    @Autowired
    private UserCouponMapper userCouponMapper;
    
    @Override
    public List<UserCoupon> getUserCoupons(Long userId, Integer status) {
        log.info("获取用户优惠券列表: userId={}, status={}", userId, status);
        
        // 查询用户优惠券
        List<UserCoupon> userCoupons = userCouponMapper.selectByUserId(userId);
        
        // 如果指定了状态，进行过滤
        if (status != null) {
            List<UserCoupon> filteredCoupons = userCoupons.stream()
                    .filter(coupon -> coupon.getStatus().equals(status))
                    .collect(Collectors.toList());
            
            log.info("根据状态过滤后，返回{}条优惠券", filteredCoupons.size());
            return filteredCoupons;
        }
        
        log.info("返回{}条优惠券", userCoupons.size());
        return userCoupons;
    }
}
