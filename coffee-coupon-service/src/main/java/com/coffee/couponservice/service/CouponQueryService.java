package com.coffee.couponservice.service;

import com.coffee.couponservice.vo.PublicCouponVO;

import java.util.List;

public interface CouponQueryService {
    List<PublicCouponVO> listAvailableCoupons();
}








