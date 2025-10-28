package com.coffee.couponservice.controller;

import com.coffee.common.result.Result;
import com.coffee.couponservice.entity.UserCoupon;
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
     */
    @GetMapping
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
            
            // 调用Service层处理业务逻辑
            List<UserCouponVO> userCoupons = userCouponService.getUserCouponVOs(userId, status);
            
            return Result.success(userCoupons);
        } catch (Exception e) {
            log.error("获取用户优惠券列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
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
     */
    @PostMapping("/{couponId}/get")
    public Result<String> claimCoupon(@PathVariable Long couponId, HttpServletRequest request) {
        try {
            // 从请求头获取用户ID（网关已验证JWT并添加到请求头）
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.isEmpty()) {
                return Result.unauthorized("未授权访问");
            }
            
            Long userId = Long.valueOf(userIdStr);
            
            // 调用Service层处理业务逻辑
            boolean success = userCouponService.claimCoupon(userId, couponId);
            
            if (success) {
                return Result.success("领取成功");
            } else {
                return Result.error("领取失败，优惠券可能已领完或您已领取过");
            }
        } catch (Exception e) {
            log.error("领取优惠券失败: {}", e.getMessage());
            return Result.error("领取失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用优惠券
     */
    @PostMapping("/{id}/use")
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
            
            // 调用Service层处理业务逻辑
            boolean success = userCouponService.useCoupon(id, userId, orderId);
            
            if (success) {
                return Result.success("使用成功");
            } else {
                return Result.error("使用失败，优惠券可能已使用或不存在");
            }
        } catch (Exception e) {
            log.error("使用优惠券失败: {}", e.getMessage());
            return Result.error("使用失败: " + e.getMessage());
        }
    }
}
