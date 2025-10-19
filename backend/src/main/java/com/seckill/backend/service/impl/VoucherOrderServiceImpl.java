package com.seckill.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ObjectMapper objectMapper;

    // Lua script (atomic stock deduction + one-order-per-user validation)
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    /**
     * Seckill entry (Redis + Lua atomic validation + MQ async order creation)
     */
    @Override
    public Result seckillVoucher(String voucherId) {
        String userId = UserContext.getUserId();
        long orderId = snowflakeIdWorker.nextId();
        // 1. Execute Lua script to validate stock and prevent duplicate orders
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId, userId, String.valueOf(orderId)
        );

        int r = result != null ? result.intValue() : -1;
        if (r == 1 || r == 3) return Result.fail(MessageConstants.OUT_OF_STOCK);
        if (r == 2) return Result.fail(MessageConstants.DUPLICATE_ORDER);

        // 2. Build order object
        VoucherOrder order = new VoucherOrder();
        order.setId(orderId);
        order.setUserId(userId);
        order.setVoucherId(voucherId);
        order.setOrderTime(LocalDateTime.now());

        // 3. Asynchronously send MQ message
        try {
            rocketMQTemplate.asyncSend(
                    "order_topic",
                    MessageBuilder.withPayload(objectMapper.writeValueAsString(order)).build(),
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("Async order message sent successfully: {}", sendResult);
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("Async order message failed to send: {}", e.getMessage(), e);
                        }
                    });
        } catch (Exception e) {
            log.error("Exception occurred while sending RocketMQ message", e);
            return Result.fail(MessageConstants.SERVER_ERROR);
        }

        return Result.ok(voucherId);
    }

    /**
     * Create actual order (called by MQ consumer)
     */
    @Override
    @Transactional
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        String userId = voucherOrder.getUserId();
        String voucherId = voucherOrder.getVoucherId();

        // 1. Idempotency check: has the user already placed an order?
        int count = voucherOrderMapper.countByUserAndVoucher(userId, voucherId);
        if (count > 0) {
            log.warn("User {} has already purchased voucher {}, rejecting duplicate order", userId, voucherId);
            return;
        }

        // 2. Deduct stock (optimistic lock)
        int stockResult = voucherOrderMapper.decrementStock(voucherId);
        if (stockResult == 0) {
            log.warn("User {} failed to purchase voucher {}: insufficient stock", userId, voucherId);
            return;
        }

        int insertResult = voucherOrderMapper.insertOrder(voucherOrder);
        if (insertResult == 1) {
            log.info("User {} successfully created order {}", userId, voucherOrder.getId());
        } else {
            log.error("User {} failed to create order: database insert failed", userId);
        }
    }
}
