package com.seckill.backend.controller;

import com.seckill.backend.common.Result;
import com.seckill.backend.entity.Voucher;
import com.seckill.backend.mapper.VoucherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private VoucherMapper voucherMapper;

    @GetMapping("/list")
    public Result<List<Voucher>> listVouchers() {
        List<Voucher> list = voucherMapper.findAll();
        return Result.ok(list);
    }
}
