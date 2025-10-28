package com.coffee.couponservice.controller;

import com.coffee.common.result.Result;
import com.coffee.couponservice.service.CouponQueryService;
import com.coffee.couponservice.vo.PublicCouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 小程序公开优惠券接口
 * 与网关配置 /public/coupons/** 相匹配：
 * 网关 stripPrefix(1) 后转发为 /coupons/**
 */
@Slf4j
@RestController
@RequestMapping("/coupons")
public class PublicCouponController {

    @Autowired
    private CouponQueryService couponQueryService;

    /**
     * 获取当前可领取/可用的优惠券列表（适配小程序字段名）
     */
    @GetMapping("/available")
    public Result<List<PublicCouponVO>> getAvailableCoupons() {
        try {
            return Result.success(couponQueryService.listAvailableCoupons());
        } catch (Exception e) {
            log.error("查询可用优惠券失败: {}", e.getMessage(), e);
            return Result.success(Collections.emptyList());
        }
    }
}
