package com.coffee.couponservice.controller;

import com.coffee.common.result.Result;
import com.coffee.couponservice.dto.SeckillRequest;
import com.coffee.couponservice.dto.SeckillResult;
import com.coffee.couponservice.entity.Coupon;
import com.coffee.couponservice.entity.SeckillActivity;
import com.coffee.couponservice.mapper.SeckillActivityMapper;
import com.coffee.couponservice.service.CouponSeckillService;
import com.coffee.couponservice.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 优惠券控制器
 */
@Slf4j
@RestController
@RequestMapping
public class CouponController {
    
    @Autowired
    private CouponSeckillService couponSeckillService;
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private SeckillActivityMapper seckillActivityMapper;
    
    /**
     * 分页查询优惠券（管理端）
     */
    @GetMapping("/coupon/page")
    public Result<Map<String, Object>> getCouponPage(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type) {
        try {
            log.info("接收优惠券列表请求: page={}, pageSize={}, name={}, status={}, type={}",
                    page, pageSize, name, status, type);
            
            // 查询优惠券列表
            List<Coupon> coupons = couponService.getCouponList(name, status, type);
            long total = couponService.countCoupons(name, status, type);
            
            // 手动分页
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, coupons.size());
            List<Coupon> pagedCoupons = coupons.stream()
                    .skip(start)
                    .limit(pageSize)
                    .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", pagedCoupons);
            result.put("total", total);
            result.put("current", page);
            result.put("size", pageSize);
            
            log.info("返回优惠券列表: 共{}条, 当前页{}条", total, pagedCoupons.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询优惠券列表失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询秒杀活动列表（管理端）
     */
    @GetMapping("/coupon/seckill/page")
    public Result<Map<String, Object>> getSeckillPage(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long couponId) {
        try {
            log.info("接收秒杀活动列表请求: page={}, pageSize={}, name={}, status={}, couponId={}",
                    page, pageSize, name, status, couponId);
            
            // 查询秒杀活动列表
            List<SeckillActivity> activities = seckillActivityMapper.selectSeckillList(name, status, couponId);
            long total = seckillActivityMapper.countSeckills(name, status, couponId);
            
            // 手动分页
            int start = (page - 1) * pageSize;
            List<SeckillActivity> pagedActivities = activities.stream()
                    .skip(start)
                    .limit(pageSize)
                    .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", pagedActivities);
            result.put("total", total);
            result.put("current", page);
            result.put("size", pageSize);
            
            log.info("返回秒杀活动列表: 共{}条, 当前页{}条", total, pagedActivities.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询秒杀活动列表失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建秒杀活动（管理端）
     */
    @PostMapping("/coupon/seckill")
    public Result<String> createSeckill(@RequestBody Map<String, Object> data) {
        try {
            log.info("创建秒杀活动: {}", data);
            
            // 创建秒杀活动对象
            SeckillActivity activity = new SeckillActivity();
            activity.setName((String) data.get("name"));
            activity.setDescription((String) data.get("description"));
            activity.setCouponId(Long.valueOf(data.get("couponId").toString()));
            activity.setSeckillStock(Integer.valueOf(data.get("seckillStock").toString()));
            activity.setPerUserLimit(Integer.valueOf(data.get("perUserLimit").toString()));
            activity.setStartTime(java.time.LocalDateTime.parse((String) data.get("startTime")));
            activity.setEndTime(java.time.LocalDateTime.parse((String) data.get("endTime")));
            activity.setStatus(1); // 默认状态为1（进行中）
            
            // 插入数据库
            seckillActivityMapper.insert(activity);
            
            log.info("创建秒杀活动成功: id={}", activity.getId());
            return Result.success("创建成功");
        } catch (Exception e) {
            log.error("创建秒杀活动失败: {}", e.getMessage(), e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户秒杀优惠券
     */
    @PostMapping("/coupon/seckill/claim")
    public Result<SeckillResult> seckillCoupon(@RequestBody SeckillRequest request) {
        try {
            return couponSeckillService.seckillCoupon(request);
        } catch (Exception e) {
            log.error("秒杀优惠券失败: {}", e.getMessage());
            return Result.error("秒杀失败: " + e.getMessage());
        }
    }
    
    /**
     * 初始化秒杀库存
     */
    @PostMapping("/coupon/init-stock")
    public Result<String> initSeckillStock(@RequestParam Long couponId, @RequestParam Integer stock) {
        try {
            couponSeckillService.initSeckillStock(couponId, stock);
            return Result.success("初始化库存成功");
        } catch (Exception e) {
            log.error("初始化库存失败: {}", e.getMessage());
            return Result.error("初始化失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取秒杀库存
     */
    @GetMapping("/coupon/stock/{couponId}")
    public Result<Integer> getSeckillStock(@PathVariable Long couponId) {
        try {
            Integer stock = couponSeckillService.getSeckillStock(couponId);
            return Result.success("获取库存成功", stock);
        } catch (Exception e) {
            log.error("获取库存失败: {}", e.getMessage());
            return Result.error("获取库存失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否已参与秒杀
     */
    @GetMapping("/coupon/check-seckill")
    public Result<Boolean> checkUserSeckill(@RequestParam Long userId, @RequestParam Long couponId) {
        try {
            boolean hasSeckill = couponSeckillService.checkUserSeckill(userId, couponId);
            return Result.success("检查成功", hasSeckill);
        } catch (Exception e) {
            log.error("检查用户秒杀状态失败: {}", e.getMessage());
            return Result.error("检查失败: " + e.getMessage());
        }
    }
}
