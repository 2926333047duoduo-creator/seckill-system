package com.seckill.backend.mq;

import com.seckill.backend.entity.VoucherOrder;
import com.seckill.backend.service.IVoucherOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RocketMQMessageListener(topic = "order_topic", consumerGroup = "order-consumer-group-v2")
public class OrderConsumerService implements RocketMQListener<VoucherOrder> {

    @Resource
    private IVoucherOrderService voucherOrderService;

    @Override
    public void onMessage(VoucherOrder voucherOrder) {
        try {
            log.info("Received async order message: user={}, voucher={}",
                    voucherOrder.getUserId(), voucherOrder.getVoucherId());
            voucherOrderService.createVoucherOrder(voucherOrder);
        } catch (Exception e) {
            log.error("Failed to process order message: {}", e.getMessage(), e);
        }
    }
}
