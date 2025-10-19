package com.seckill.backend.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.backend.common.Result;
import com.seckill.backend.entity.VoucherOrder;

public interface IVoucherOrderService {

    Result seckillVoucher( String voucherId);

    void createVoucherOrder(VoucherOrder voucherOrder);
}
