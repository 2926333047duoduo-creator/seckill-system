package com.seckill.backend.controller;

import com.seckill.backend.common.Result;
import com.seckill.backend.context.UserContext;
import com.seckill.backend.entity.Voucher;
import com.seckill.backend.mapper.VoucherMapper;
import com.seckill.backend.mapper.VoucherOrderMapper;
import com.seckill.backend.service.IVoucherOrderService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private VoucherMapper voucherMapper;
    @Autowired
    private VoucherOrderMapper voucherOrderMapper;
    @GetMapping("/list")
    public Result<List<Voucher>> listVouchers() {
        List<Voucher> list = voucherMapper.findAll();
        return Result.ok(list);
    }
    @GetMapping("/myVoucher")
    public Result<List<Voucher>> myVoucher() {
        String id = UserContext.getUserId();
        List<Voucher> list = voucherMapper.getById(id);
        return Result.ok(list);
    }
    @Resource
    private IVoucherOrderService voucherOrderService;
    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id")  String voucherId) {
        return voucherOrderService.seckillVoucher(voucherId);
    }
}
