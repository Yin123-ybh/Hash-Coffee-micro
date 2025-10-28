package com.coffee.couponservice.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.coffee.common.result.Result;
import com.coffee.couponservice.handler.SentinelFallbackHandler;
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
     * 
     * @SentinelResource 注解说明：
     * - value: 资源名称，用于流量控制和熔断降级
     * - blockHandler: 流控触发时的降级处理方法
     * - fallback: 业务异常时的降级处理方法
     * - blockHandlerClass: 降级处理类
     */
    @GetMapping("/available")
    @SentinelResource(
        value = "queryCoupon",
        blockHandler = "getAvailableCouponsBlockHandler",
        fallback = "getAvailableCouponsFallback",
        blockHandlerClass = SentinelFallbackHandler.class
    )
    public Result<List<PublicCouponVO>> getAvailableCoupons() {
        try {
            log.info("🔍 开始查询可用优惠券列表");
            List<PublicCouponVO> coupons = couponQueryService.listAvailableCoupons();
            log.info("✅ 查询到 {} 个可用优惠券", coupons.size());
            return Result.success(coupons);
        } catch (Exception e) {
            log.error("💥 查询可用优惠券失败: {}", e.getMessage(), e);
            // 这里抛出异常，会触发fallback方法
            throw new RuntimeException("查询优惠券服务异常", e);
        }
    }
}
