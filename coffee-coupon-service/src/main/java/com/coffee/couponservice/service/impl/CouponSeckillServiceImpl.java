package com.coffee.couponservice.service.impl;

import com.coffee.common.result.Result;
import com.coffee.couponservice.dto.SeckillRequest;
import com.coffee.couponservice.dto.SeckillResult;
import com.coffee.couponservice.entity.Coupon;
import com.coffee.couponservice.mapper.CouponMapper;
import com.coffee.couponservice.service.CouponSeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 优惠券秒杀服务实现类
 * 使用Redis + Lua脚本 + Redisson分布式锁防止超卖
 */
@Slf4j
@Service
public class CouponSeckillServiceImpl implements CouponSeckillService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedissonClient redissonClient;
    
    @Autowired
    private CouponMapper couponMapper;
    
    // Lua脚本：原子性扣减库存
    private DefaultRedisScript<Long> seckillScript;
    
    public CouponSeckillServiceImpl() {
        // 初始化Lua脚本
        seckillScript = new DefaultRedisScript<>();
        seckillScript.setResultType(Long.class);
        
        try {
            ClassPathResource resource = new ClassPathResource("lua/seckill.lua");
            String script = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            seckillScript.setScriptText(script);
        } catch (Exception e) {
            log.error("加载Lua脚本失败", e);
            // 使用内联脚本作为备用
            seckillScript.setScriptText(getInlineSeckillScript());
        }
    }
    
    @Override
    public Result<SeckillResult> seckillCoupon(SeckillRequest request) {
        Long userId = request.getUserId();
        Long couponId = request.getCouponId();
        
        log.info("用户 {} 开始秒杀优惠券 {}", userId, couponId);
        
        // 1. 检查用户是否已参与秒杀
        if (checkUserSeckill(userId, couponId)) {
            return Result.error("您已经参与过该优惠券的秒杀活动");
        }
        
        // 2. 使用分布式锁防止重复请求
        String lockKey = "seckill:lock:" + userId + ":" + couponId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 尝试获取锁，最多等待1秒，锁定时间10秒
            if (lock.tryLock(1, 10, TimeUnit.SECONDS)) {
                // 3. 使用Lua脚本原子性扣减库存
                String stockKey = "seckill:stock:" + couponId;
                String userKey = "seckill:user:" + couponId;
                
                Long result = redisTemplate.execute(seckillScript, 
                    Arrays.asList(stockKey, userKey), 
                    userId.toString(), 
                    System.currentTimeMillis());
                
                if (result != null && result == 1) {
                    // 秒杀成功
                    log.info("用户 {} 秒杀优惠券 {} 成功", userId, couponId);
                    
                    SeckillResult seckillResult = new SeckillResult();
                    seckillResult.setSuccess(true);
                    seckillResult.setCouponId(couponId);
                    seckillResult.setUserId(userId);
                    seckillResult.setMessage("秒杀成功");
                    
                    // 同步更新数据库库存
                    syncDatabaseStock(couponId);
                    
                    // 异步处理后续业务逻辑（发送MQ消息）
                    // 这里可以发送消息到RabbitMQ进行异步处理
                    
                    return Result.success("秒杀成功", seckillResult);
                } else {
                    // 秒杀失败
                    log.info("用户 {} 秒杀优惠券 {} 失败，库存不足或已参与", userId, couponId);
                    return Result.error("秒杀失败，库存不足或您已参与过该活动");
                }
            } else {
                return Result.error("系统繁忙，请稍后重试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断", e);
            return Result.error("系统异常，请稍后重试");
        } catch (Exception e) {
            log.error("秒杀过程中发生异常", e);
            return Result.error("系统异常，请稍后重试");
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    @Override
    public void initSeckillStock(Long couponId, Integer stock) {
        String stockKey = "seckill:stock:" + couponId;
        redisTemplate.opsForValue().set(stockKey, stock, 24, TimeUnit.HOURS);
        log.info("初始化优惠券 {} 秒杀库存: {}", couponId, stock);
    }
    
    @Override
    public Integer getSeckillStock(Long couponId) {
        String stockKey = "seckill:stock:" + couponId;
        Object stock = redisTemplate.opsForValue().get(stockKey);
        return stock != null ? (Integer) stock : 0;
    }
    
    @Override
    public boolean checkUserSeckill(Long userId, Long couponId) {
        String userKey = "seckill:user:" + couponId;
        return redisTemplate.opsForSet().isMember(userKey, userId);
    }
    
    /**
     * 内联Lua脚本（备用方案）
     */
    private String getInlineSeckillScript() {
        return "local stockKey = KEYS[1]\n" +
               "local userKey = KEYS[2]\n" +
               "local userId = ARGV[1]\n" +
               "local timestamp = ARGV[2]\n" +
               "\n" +
               "-- 检查用户是否已参与\n" +
               "if redis.call('SISMEMBER', userKey, userId) == 1 then\n" +
               "    return 0\n" +
               "end\n" +
               "\n" +
               "-- 检查库存\n" +
               "local stock = redis.call('GET', stockKey)\n" +
               "if not stock or tonumber(stock) <= 0 then\n" +
               "    return 0\n" +
               "end\n" +
               "\n" +
               "-- 扣减库存\n" +
               "local newStock = redis.call('DECR', stockKey)\n" +
               "if newStock >= 0 then\n" +
               "    -- 记录用户参与\n" +
               "    redis.call('SADD', userKey, userId)\n" +
               "    -- 设置用户参与时间\n" +
               "    redis.call('HSET', userKey .. ':time', userId, timestamp)\n" +
               "    return 1\n" +
               "else\n" +
               "    -- 库存不足，回滚\n" +
               "    redis.call('INCR', stockKey)\n" +
               "    return 0\n" +
               "end";
    }
    
    /**
     * 同步数据库库存
     * 根据Redis中的库存更新数据库中的used_count
     */
    private void syncDatabaseStock(Long couponId) {
        try {
            // 获取Redis中的剩余库存
            Integer redisStock = getSeckillStock(couponId);
            if (redisStock == null) {
                log.warn("Redis中未找到优惠券 {} 的库存信息", couponId);
                return;
            }
            
            // 获取数据库中的优惠券信息
            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon == null) {
                log.warn("数据库中未找到优惠券 {}", couponId);
                return;
            }
            
            // 计算已使用数量
            Integer totalCount = coupon.getTotalCount() != null ? coupon.getTotalCount() : 0;
            Integer usedCount = Math.max(0, totalCount - redisStock);
            
            // 更新数据库
            couponMapper.updateUsedCount(couponId, usedCount);
            
            log.info("同步数据库库存成功: 优惠券ID={}, 总库存={}, 已使用={}, 剩余={}", 
                    couponId, totalCount, usedCount, redisStock);
        } catch (Exception e) {
            log.error("同步数据库库存失败: 优惠券ID={}", couponId, e);
        }
    }
}

