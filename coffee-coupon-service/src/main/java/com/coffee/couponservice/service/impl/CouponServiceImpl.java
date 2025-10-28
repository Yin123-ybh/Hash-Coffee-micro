package com.coffee.couponservice.service.impl;

import com.coffee.couponservice.entity.Coupon;
import com.coffee.couponservice.mapper.CouponMapper;
import com.coffee.couponservice.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 优惠券服务实现类
 */
@Slf4j
@Service
public class CouponServiceImpl implements CouponService {
    
    @Autowired
    private CouponMapper couponMapper;
    
    @Override
    public List<Coupon> getCouponList(String name, Integer status, Integer type) {
        log.info("查询优惠券列表: name={}, status={}, type={}", name, status, type);
        return couponMapper.selectCouponList(name, status, type);
    }
    
    @Override
    public long countCoupons(String name, Integer status, Integer type) {
        log.info("统计优惠券总数: name={}, status={}, type={}", name, status, type);
        return couponMapper.countCoupons(name, status, type);
    }
    
    @Override
    public Coupon getCouponById(Long id) {
        log.info("查询优惠券: id={}", id);
        return couponMapper.selectById(id);
    }
}

