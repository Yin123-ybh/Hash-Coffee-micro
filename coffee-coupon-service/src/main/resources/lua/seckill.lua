-- 优惠券秒杀Lua脚本
-- 防止超卖的原子性操作

local stockKey = KEYS[1]      -- 库存键
local userKey = KEYS[2]       -- 用户集合键
local userId = ARGV[1]        -- 用户ID
local timestamp = ARGV[2]     -- 时间戳

-- 检查用户是否已参与秒杀
if redis.call('SISMEMBER', userKey, userId) == 1 then
    return 0
end

-- 获取当前库存
local stock = redis.call('GET', stockKey)
if not stock or tonumber(stock) <= 0 then
    return 0
end

-- 原子性扣减库存
local newStock = redis.call('DECR', stockKey)
if newStock >= 0 then
    -- 记录用户参与秒杀
    redis.call('SADD', userKey, userId)
    
    -- 记录用户参与时间（用于后续分析）
    redis.call('HSET', userKey .. ':time', userId, timestamp)
    
    -- 设置用户参与记录过期时间（24小时）
    redis.call('EXPIRE', userKey .. ':time', 86400)
    
    return 1
else
    -- 库存不足，回滚操作
    redis.call('INCR', stockKey)
    return 0
end
