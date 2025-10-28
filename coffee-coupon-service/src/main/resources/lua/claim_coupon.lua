-- 优惠券领取Lua脚本
-- 防止超卖和重复领取的原子性操作

local stockKey = KEYS[1]        -- 库存键
local userKey = KEYS[2]         -- 用户领取记录键
local userId = ARGV[1]          -- 用户ID
local couponId = ARGV[2]        -- 优惠券ID
local timestamp = ARGV[3]       -- 时间戳

-- 检查用户是否已领取过该优惠券
if redis.call('SISMEMBER', userKey, userId) == 1 then
    return 0  -- 已领取过
end

-- 获取当前库存
local stock = redis.call('GET', stockKey)
if not stock or tonumber(stock) <= 0 then
    return 0  -- 库存不足
end

-- 原子性扣减库存
local newStock = redis.call('DECR', stockKey)
if newStock >= 0 then
    -- 记录用户领取记录
    redis.call('SADD', userKey, userId)
    
    -- 记录用户领取时间
    redis.call('HSET', userKey .. ':time', userId, timestamp)
    
    -- 设置用户领取记录过期时间（30天）
    redis.call('EXPIRE', userKey .. ':time', 2592000)
    
    return 1  -- 领取成功
else
    -- 库存不足，回滚操作
    redis.call('INCR', stockKey)
    return 0  -- 领取失败
end
