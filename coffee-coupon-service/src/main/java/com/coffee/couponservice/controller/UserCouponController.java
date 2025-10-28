package com.coffee.couponservice.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.coffee.common.result.Result;
import com.coffee.couponservice.entity.UserCoupon;
import com.coffee.couponservice.handler.SentinelFallbackHandler;
import com.coffee.couponservice.service.UserCouponService;
import com.coffee.couponservice.vo.UserCouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户优惠券控制器
 */
@Slf4j
@RestController
@RequestMapping("/user/coupons")
public class UserCouponController {
    
    @Autowired
    private UserCouponService userCouponService;
    
    /**
     * 获取用户优惠券列表
     * 
     * @SentinelResource 注解说明：
     * - value: 资源名称，用于流量控制和熔断降级
     * - blockHandler: 流控触发时的降级处理方法
     * - fallback: 业务异常时的降级处理方法
     */
    @GetMapping
    @SentinelResource(
        value = "getUserCoupons",
        blockHandler = "getUserCouponsBlockHandler",
        fallback = "getUserCouponsFallback",
        blockHandlerClass = SentinelFallbackHandler.class
    )
    public Result<List<UserCouponVO>> getUserCoupons(
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        try {
            // 从请求头获取用户ID（网关已验证JWT并添加到请求头）
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.isEmpty()) {
                return Result.unauthorized("未授权访问");
            }
            
            Long userId = Long.valueOf(userIdStr);
            log.info("🔍 开始查询用户{}的优惠券列表，状态：{}", userId, status);
            
            // 调用Service层处理业务逻辑
            List<UserCouponVO> userCoupons = userCouponService.getUserCouponVOs(userId, status);
            
            log.info("✅ 查询到用户{}的{}个优惠券", userId, userCoupons.size());
            return Result.success(userCoupons);
        } catch (Exception e) {
            log.error("💥 获取用户优惠券失败: {}", e.getMessage(), e);
            // 抛出异常，触发fallback方法
            throw new RuntimeException("获取用户优惠券服务异常", e);
        }
    }
    
    /**
     * 获取用户优惠券详情
     */
    @GetMapping("/{id}")
    public Result<UserCouponVO> getUserCouponDetail(@PathVariable Long id, HttpServletRequest request) {
        try {
            // 从请求头获取用户ID（网关已验证JWT并添加到请求头）
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.isEmpty()) {
                return Result.unauthorized("未授权访问");
            }
            
            Long userId = Long.valueOf(userIdStr);
            
            // 调用Service层处理业务逻辑
            List<UserCouponVO> userCoupons = userCouponService.getUserCouponVOs(userId, null);
            
            // 查找指定ID的用户优惠券
            UserCouponVO userCoupon = userCoupons.stream()
                    .filter(coupon -> coupon.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            
            if (userCoupon == null) {
                return Result.error("优惠券不存在或不属于当前用户");
            }
            
            return Result.success(userCoupon);
        } catch (Exception e) {
            log.error("获取用户优惠券详情失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 领取优惠券
     * 
     * @SentinelResource 注解说明：
     * - value: 资源名称，用于流量控制和熔断降级
     * - blockHandler: 流控触发时的降级处理方法
     * - fallback: 业务异常时的降级处理方法
     */
    @PostMapping("/{couponId}/get")
    @SentinelResource(
        value = "claimCoupon",
        blockHandler = "claimCouponBlockHandler",
        fallback = "claimCouponFallback",
        blockHandlerClass = SentinelFallbackHandler.class
    )
    public Result<String> claimCoupon(@PathVariable Long couponId, HttpServletRequest request) {
        try {
            // 从请求头获取用户ID（网关已验证JWT并添加到请求头）
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.isEmpty()) {
                return Result.unauthorized("未授权访问");
            }
            
            Long userId = Long.valueOf(userIdStr);
            log.info("🎫 用户{}开始领取优惠券{}", userId, couponId);
            
            // 调用Service层处理业务逻辑
            boolean success = userCouponService.claimCoupon(userId, couponId);
            
            if (success) {
                log.info("✅ 用户{}成功领取优惠券{}", userId, couponId);
                return Result.success("领取成功");
            } else {
                log.warn("❌ 用户{}领取优惠券{}失败", userId, couponId);
                return Result.error("领取失败，优惠券可能已领完或您已领取过");
            }
        } catch (Exception e) {
            log.error("💥 领取优惠券失败: {}", e.getMessage(), e);
            // 抛出异常，触发fallback方法
            throw new RuntimeException("领取优惠券服务异常", e);
        }
    }
    
    /**
     * 使用优惠券
     * 
     * @SentinelResource 注解说明：
     * - value: 资源名称，用于流量控制和熔断降级
     * - blockHandler: 流控触发时的降级处理方法
     * - fallback: 业务异常时的降级处理方法
     */
    @PostMapping("/{id}/use")
    @SentinelResource(
        value = "useCoupon",
        blockHandler = "useCouponBlockHandler",
        fallback = "useCouponFallback",
        blockHandlerClass = SentinelFallbackHandler.class
    )
    public Result<String> useCoupon(@PathVariable Long id, 
                                   @RequestParam Long orderId, 
                                   HttpServletRequest request) {
        try {
            // 从请求头获取用户ID（网关已验证JWT并添加到请求头）
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.isEmpty()) {
                return Result.unauthorized("未授权访问");
            }
            
            Long userId = Long.valueOf(userIdStr);
            log.info("💳 用户{}开始使用优惠券{}，订单ID：{}", userId, id, orderId);
            
            // 调用Service层处理业务逻辑
            boolean success = userCouponService.useCoupon(id, userId, orderId);
            
            if (success) {
                log.info("✅ 用户{}成功使用优惠券{}", userId, id);
                return Result.success("使用成功");
            } else {
                log.warn("❌ 用户{}使用优惠券{}失败", userId, id);
                return Result.error("使用失败，优惠券可能已使用或不存在");
            }
        } catch (Exception e) {
            log.error("💥 使用优惠券失败: {}", e.getMessage(), e);
            // 抛出异常，触发fallback方法
            throw new RuntimeException("使用优惠券服务异常", e);
        }
    }
}
