package com.coffee.couponservice.service.impl;

import com.coffee.couponservice.entity.Coupon;
import com.coffee.couponservice.entity.UserCoupon;
import com.coffee.couponservice.mapper.CouponMapper;
import com.coffee.couponservice.mapper.UserCouponMapper;
import com.coffee.couponservice.service.UserCouponService;
import com.coffee.couponservice.vo.UserCouponVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户优惠券服务实现类
 */
@Slf4j
@Service
public class UserCouponServiceImpl implements UserCouponService {
    
    @Autowired
    private UserCouponMapper userCouponMapper;
    
    @Autowired
    private CouponMapper couponMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedissonClient redissonClient;
    
    // Lua脚本：原子性领取优惠券
    private DefaultRedisScript<Long> claimScript;
    
    public UserCouponServiceImpl() {
        // 初始化Lua脚本
        claimScript = new DefaultRedisScript<>();
        claimScript.setResultType(Long.class);
        
        try {
            ClassPathResource resource = new ClassPathResource("lua/claim_coupon.lua");
            String script = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            claimScript.setScriptText(script);
        } catch (Exception e) {
            log.error("加载领取优惠券Lua脚本失败", e);
            // 使用内联脚本作为备用
            claimScript.setScriptText(getInlineClaimScript());
        }
    }
    
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
    
    @Override
    public List<UserCouponVO> getUserCouponVOs(Long userId, Integer status) {
        log.info("获取用户优惠券VO列表: userId={}, status={}", userId, status);
        
        // 查询用户优惠券
        List<UserCoupon> userCoupons = userCouponMapper.selectByUserId(userId);
        
        // 如果指定了状态，进行过滤
        if (status != null) {
            userCoupons = userCoupons.stream()
                    .filter(coupon -> coupon.getStatus().equals(status))
                    .collect(Collectors.toList());
        }
        
        // 转换为VO
        List<UserCouponVO> userCouponVOs = userCoupons.stream().map(userCoupon -> {
            UserCouponVO vo = new UserCouponVO();
            
            // 设置用户优惠券基本信息
            vo.setId(userCoupon.getId());
            vo.setUserId(userCoupon.getUserId());
            vo.setCouponId(userCoupon.getCouponId());
            vo.setStatus(userCoupon.getStatus());
            vo.setUsedTime(userCoupon.getUsedTime());
            vo.setOrderId(userCoupon.getOrderId());
            vo.setCreateTime(userCoupon.getCreateTime());
            vo.setUpdateTime(userCoupon.getUpdateTime());
            
            // 查询优惠券详情
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
            if (coupon != null) {
                vo.setName(coupon.getName());
                vo.setTitle(coupon.getName()); // 标题使用名称
                vo.setType(coupon.getType());
                vo.setDiscountAmount(BigDecimal.valueOf(coupon.getDiscountValue()));
                vo.setMinAmount(BigDecimal.valueOf(coupon.getMinAmount()));
                vo.setStartTime(coupon.getStartTime());
                vo.setEndTime(coupon.getEndTime());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        log.info("返回{}条优惠券VO", userCouponVOs.size());
        return userCouponVOs;
    }
    
    @Override
    @Transactional
    public boolean claimCoupon(Long userId, Long couponId) {
        log.info("用户{}领取优惠券{}", userId, couponId);
        
        // 1. 检查优惠券是否存在且可用
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            log.warn("优惠券{}不存在", couponId);
            return false;
        }
        
        if (coupon.getStatus() != 1) {
            log.warn("优惠券{}状态不可用: {}", couponId, coupon.getStatus());
            return false;
        }
        
        // 2. 检查优惠券是否在有效期内
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            log.warn("优惠券{}不在有效期内", couponId);
            return false;
        }
        
        // 3. 初始化Redis库存（如果不存在）
        initCouponStock(couponId, coupon);
        
        // 4. 使用分布式锁防止重复请求
        String lockKey = "claim:lock:" + userId + ":" + couponId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 尝试获取锁，最多等待1秒，锁定时间10秒
            if (lock.tryLock(1, 10, TimeUnit.SECONDS)) {
                // 5. 使用Lua脚本原子性领取优惠券
                String stockKey = "coupon:stock:" + couponId;
                String userKey = "coupon:user:" + couponId;
                
                Long result = redisTemplate.execute(claimScript, 
                    Arrays.asList(stockKey, userKey), 
                    userId.toString(), 
                    couponId.toString(),
                    System.currentTimeMillis());
                
                if (result != null && result == 1) {
                    // 领取成功，同步到数据库
                    syncClaimToDatabase(userId, couponId, now);
                    log.info("用户{}成功领取优惠券{}", userId, couponId);
                    return true;
                } else {
                    // 领取失败
                    log.warn("用户{}领取优惠券{}失败，库存不足或已领取过", userId, couponId);
                    return false;
                }
            } else {
                log.warn("用户{}获取领取锁失败", userId);
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断", e);
            return false;
        } catch (Exception e) {
            log.error("领取优惠券过程中发生异常", e);
            throw e;
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    @Override
    @Transactional
    public boolean useCoupon(Long userCouponId, Long userId, Long orderId) {
        log.info("用户{}使用优惠券{}，订单ID: {}", userId, userCouponId, orderId);
        
        try {
            // 1. 查询用户优惠券
            UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
            if (userCoupon == null) {
                log.warn("用户优惠券{}不存在", userCouponId);
                return false;
            }
            
            // 2. 检查是否属于当前用户
            if (!userCoupon.getUserId().equals(userId)) {
                log.warn("用户{}无权使用优惠券{}", userId, userCouponId);
                return false;
            }
            
            // 3. 检查优惠券状态
            if (userCoupon.getStatus() != 1) {
                log.warn("优惠券{}状态不可用: {}", userCouponId, userCoupon.getStatus());
                return false;
            }
            
            // 4. 更新优惠券状态为已使用
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            int updateResult = userCouponMapper.updateStatus(userCouponId, 2, orderId);
            if (updateResult <= 0) {
                log.error("更新优惠券状态失败");
                return false;
            }
            
            log.info("用户{}成功使用优惠券{}", userId, userCouponId);
            return true;
            
        } catch (Exception e) {
            log.error("使用优惠券失败: userCouponId={}, userId={}, orderId={}", userCouponId, userId, orderId, e);
            throw e;
        }
    }
    
    /**
     * 初始化优惠券库存到Redis
     */
    private void initCouponStock(Long couponId, Coupon coupon) {
        String stockKey = "coupon:stock:" + couponId;
        if (!redisTemplate.hasKey(stockKey)) {
            Integer totalCount = coupon.getTotalCount() != null ? coupon.getTotalCount() : 0;
            Integer usedCount = coupon.getUsedCount() != null ? coupon.getUsedCount() : 0;
            Integer remainingStock = Math.max(0, totalCount - usedCount);
            
            redisTemplate.opsForValue().set(stockKey, remainingStock);
            log.info("初始化优惠券{}库存到Redis: {}", couponId, remainingStock);
        }
    }
    
    /**
     * 同步领取记录到数据库
     */
    private void syncClaimToDatabase(Long userId, Long couponId, java.time.LocalDateTime now) {
        try {
            // 1. 创建用户优惠券记录
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(couponId);
            userCoupon.setStatus(1); // 1-未使用
            userCoupon.setCreateTime(now);
            userCoupon.setUpdateTime(now);
            
            int insertResult = userCouponMapper.insert(userCoupon);
            if (insertResult <= 0) {
                log.error("插入用户优惠券记录失败");
                throw new RuntimeException("插入用户优惠券记录失败");
            }
            
            // 2. 使用乐观锁更新优惠券使用数量
            int updateResult = couponMapper.updateUsedCountOptimistic(couponId);
            if (updateResult <= 0) {
                log.error("更新优惠券使用数量失败，可能库存不足或状态异常");
                throw new RuntimeException("更新优惠券使用数量失败");
            }
            
            log.info("同步领取记录到数据库成功: userId={}, couponId={}", userId, couponId);
        } catch (Exception e) {
            log.error("同步领取记录到数据库失败: userId={}, couponId={}", userId, couponId, e);
            throw e;
        }
    }
    
    /**
     * 内联Lua脚本（备用）
     */
    private String getInlineClaimScript() {
        return "-- 优惠券领取Lua脚本\n" +
                "local stockKey = KEYS[1]\n" +
                "local userKey = KEYS[2]\n" +
                "local userId = ARGV[1]\n" +
                "local couponId = ARGV[2]\n" +
                "local timestamp = ARGV[3]\n" +
                "\n" +
                "-- 检查用户是否已领取过该优惠券\n" +
                "if redis.call('SISMEMBER', userKey, userId) == 1 then\n" +
                "    return 0\n" +
                "end\n" +
                "\n" +
                "-- 获取当前库存\n" +
                "local stock = redis.call('GET', stockKey)\n" +
                "if not stock or tonumber(stock) <= 0 then\n" +
                "    return 0\n" +
                "end\n" +
                "\n" +
                "-- 原子性扣减库存\n" +
                "local newStock = redis.call('DECR', stockKey)\n" +
                "if newStock >= 0 then\n" +
                "    redis.call('SADD', userKey, userId)\n" +
                "    redis.call('HSET', userKey .. ':time', userId, timestamp)\n" +
                "    redis.call('EXPIRE', userKey .. ':time', 2592000)\n" +
                "    return 1\n" +
                "else\n" +
                "    redis.call('INCR', stockKey)\n" +
                "    return 0\n" +
                "end";
    }
}

