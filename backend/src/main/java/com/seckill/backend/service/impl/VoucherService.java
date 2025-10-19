package com.seckill.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.backend.entity.Voucher;
import com.seckill.backend.mapper.VoucherMapper;
import com.seckill.backend.service.IVoucherService;
import org.springframework.stereotype.Service;

@Service
public class VoucherService extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {
}
