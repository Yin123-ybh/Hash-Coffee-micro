package com.coffee.couponservice.controller;

import com.coffee.common.result.Result;
import com.coffee.couponservice.entity.UserCoupon;
import com.coffee.couponservice.service.UserCouponService;
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
    public Result<List<UserCoupon>> getUserCoupons(
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
            List<UserCoupon> userCoupons = userCouponService.getUserCoupons(userId, status);
            
            return Result.success(userCoupons);
        } catch (Exception e) {
            log.error("获取用户优惠券列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
