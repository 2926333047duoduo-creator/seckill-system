-- ARGV[1] : voucherId
-- ARGV[2] : userId
-- ARGV[3] : orderId (可选)
local voucherId = ARGV[1]
local userId = ARGV[2]
local orderId = ARGV[3]

-- 2. 构造 Redis Key
local stockKey = 'seckill:stock:' .. voucherId
local orderKey = 'seckill:order:' .. voucherId

-- 3. 检查库存是否存在
local stock = redis.call('get', stockKey)
if not stock then
    -- key 不存在 => 库存未初始化
    return 3
end

-- 转为数字
stock = tonumber(stock)
if stock <= 0 then
    -- 库存不足 => 删除库存 key（防止脚本再次访问空值）
    redis.call('del', stockKey)
    return 1
end

-- 4. 检查是否重复下单
if redis.call('sismember', orderKey, userId) == 1 then
    -- 已经买过 => 重复下单
    return 2
end

-- 5. 扣减库存
redis.call('incrby', stockKey, -1)

-- 6. 如果扣减后库存为 0 => 删除库存 key
if (redis.call('get', stockKey) == '0') then
    redis.call('del', stockKey)
end

-- 7. 记录下单用户
redis.call('sadd', orderKey, userId)

-- 8. 成功
return 0
