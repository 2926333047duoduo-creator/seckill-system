package com.seckill.backend.controller;

import com.seckill.backend.common.MessageConstants;
import com.seckill.backend.common.Result;
import com.seckill.backend.dto.VoucherDTO;
import com.seckill.backend.entity.Voucher;
import com.seckill.backend.mapper.VoucherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private VoucherMapper voucherMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Create a new voucher
     */
    @PostMapping("/add")
    public Result<String> addVoucher(@RequestBody VoucherDTO dto) {
        try {
            // 校验开始时间不能早于当前时间
            if (dto.getStartTime() == null || dto.getStartTime().isBefore(LocalDateTime.now())) {
                return Result.fail("开始时间不能早于当前时间");
            }

            // 构建 Voucher 实体
            Voucher voucher = new Voucher();
            String uuid = UUID.randomUUID().toString();
            voucher.setId(uuid);
            voucher.setName(dto.getName());
            voucher.setAmount(dto.getAmount());
            voucher.setTotal(dto.getTotal());
            voucher.setStock(dto.getStock());
            voucher.setStartTime(dto.getStartTime());
            voucher.setCreateTime(LocalDateTime.now());

            //  插入数据库
            voucherMapper.insert(voucher);

            //同步库存到 Redis（关键）
            String stockKey = "seckill:stock:" + uuid;
            stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(dto.getStock()));

            // 返回生成的 UUID
            return Result.ok(uuid);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MessageConstants.SERVER_ERROR);
        }
    }


    /**
     * Update voucher (only name and start_time, and only before start_time)
     */
    @PostMapping("/update")
    public Result<String> updateVoucher( @RequestBody VoucherDTO dto) {
        Voucher existing = voucherMapper.findById(dto.getId());
        if (existing == null) {
            return Result.fail(MessageConstants.VOUCHER_NOT_FOUND);
        }

        if (LocalDateTime.now().isAfter(existing.getStartTime())) {
            return Result.fail(MessageConstants.VOUCHER_UPDATE_DENIED);
        }

        try {
            existing.setName(dto.getName());
            existing.setStartTime(dto.getStartTime());
            existing.setStock(dto.getStock());
            voucherMapper.update(existing);
            return Result.ok(MessageConstants.VOUCHER_UPDATED_SUCCESS);
        } catch (Exception e) {
            return Result.fail(MessageConstants.SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteVoucher(@PathVariable String id) {
        // 1️⃣ 查询是否存在
        Voucher existing = voucherMapper.findById(id);
        if (existing == null) {
            return Result.fail(MessageConstants.VOUCHER_NOT_FOUND);
        }

        // 2️⃣ 只允许在开始时间之前删除
        if (LocalDateTime.now().isAfter(existing.getStartTime())) {
            return Result.fail(MessageConstants.VOUCHER_DELETE_DENIED);
        }

        try {
            // 3️⃣ 删除数据库记录
            voucherMapper.delete(id);

            // 4️⃣ 同步删除 Redis 库存 key
            String stockKey = "seckill:stock:" + id;
            stringRedisTemplate.delete(stockKey);

            // 5️⃣ 同步删除 Redis 用户下单集合（防止旧记录干扰）
            String orderKey = "seckill:order:" + id;
            stringRedisTemplate.delete(orderKey);

            return Result.ok(MessageConstants.VOUCHER_DELETED_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MessageConstants.SERVER_ERROR);
        }
    }

    /**
     * Get all vouchers
     */
    @GetMapping("/list")
    public Result<List<Voucher>> listVouchers() {
        List<Voucher> list = voucherMapper.findAll();
        return Result.ok(list);
    }
}
