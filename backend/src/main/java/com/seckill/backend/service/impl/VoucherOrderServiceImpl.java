package com.seckill.backend.service.impl;

import com.seckill.backend.common.MessageConstants;
import com.seckill.backend.common.Result;
import com.seckill.backend.context.UserContext;
import com.seckill.backend.entity.VoucherOrder;
import com.seckill.backend.mapper.VoucherOrderMapper;
import com.seckill.backend.service.IVoucherOrderService;
import com.seckill.backend.utils.SnowflakeIdWorker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Service
public class VoucherOrderServiceImpl implements IVoucherOrderService {

    @Resource
    private VoucherOrderMapper voucherOrderMapper;
    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    // Lua 脚本（库存扣减 + 一人一单判断）
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    /**
     * 秒杀入口（Redis + Lua 原子校验 + MQ 异步下单）
     */
    @Override
    public Result seckillVoucher(String voucherId) {
        String userId = UserContext.getUserId();
        long orderId = snowflakeIdWorker.nextId();
        // 1. 执行 Lua 脚本校验库存和重复下单
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId, userId, String.valueOf(orderId)
        );

        int r = result != null ? result.intValue() : -1;
        if (r == 1 || r == 3) return Result.fail(MessageConstants.OUT_OF_STOCK);
        if (r == 2) return Result.fail(MessageConstants.DUPLICATE_ORDER);

        // 2. 构建订单对象
        VoucherOrder order = new VoucherOrder();
        order.setId(orderId);
        order.setUserId(userId);
        order.setVoucherId(voucherId);
        order.setOrderTime(LocalDateTime.now());

        // 3. 异步发送 MQ 消息
        try {
            rocketMQTemplate.asyncSend(
                    "order_topic",
                    MessageBuilder.withPayload(order).build(),
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("异步下单消息发送成功：{}", sendResult);
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("异步下单消息发送失败：{}", e.getMessage(), e);
                        }
                    });
        } catch (Exception e) {
            log.error("发送 RocketMQ 消息异常", e);
            return Result.fail(MessageConstants.SERVER_ERROR);
        }

        return Result.ok(result);
    }

    /**
     * 真正创建订单（由 MQ 消费者调用）
     */
    @Override
    @Transactional
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        String userId = voucherOrder.getUserId();
        String voucherId = voucherOrder.getVoucherId();

        // 1. 幂等校验：是否已下过单
        int count = voucherOrderMapper.countByUserAndVoucher(userId, voucherId);
        if (count > 0) {
            log.warn("用户 {} 已抢购过优惠券 {}，拒绝重复下单", userId, voucherId);
            return;
        }

        // 2. 扣减库存（乐观锁）
        int stockResult = voucherOrderMapper.decrementStock(voucherId);
        if (stockResult == 0) {
            log.warn("用户 {} 抢购优惠券 {} 失败：库存不足", userId, voucherId);
            return;
        }

        int insertResult = voucherOrderMapper.insertOrder(voucherOrder);
        if (insertResult == 1) {
            log.info("用户 {} 成功创建订单 {}", userId, voucherOrder.getId());
        } else {
            log.error("用户 {} 创建订单失败：数据库插入失败", userId);
        }
    }
}
