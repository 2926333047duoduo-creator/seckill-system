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
import java.util.Arrays;
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
     * 创建新的优惠券
     */
    @PostMapping("/add")
    public Result<String> addVoucher(@RequestBody VoucherDTO dto) {
        try {
            // 1. 校验开始时间
            if (dto.getStartTime() == null || dto.getStartTime().isBefore(LocalDateTime.now())) {
                return Result.fail("开始时间不能早于当前时间");
            }

            // 2. 构建实体对象
            Voucher voucher = new Voucher();
            String uuid = UUID.randomUUID().toString();
            voucher.setId(uuid);
            voucher.setName(dto.getName());
            voucher.setAmount(dto.getAmount());
            voucher.setTotal(dto.getTotal());
            voucher.setStock(dto.getStock());
            voucher.setStartTime(dto.getStartTime());
            voucher.setCreateTime(LocalDateTime.now());

            // 3. 插入数据库
            voucherMapper.insert(voucher);

            // 4. 初始化 Redis 秒杀库存 key
            String stockKey = "seckill:stock:" + uuid;
            stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(dto.getStock()));

            return Result.ok(uuid);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MessageConstants.SERVER_ERROR);
        }
    }

    /**
     * 更新优惠券信息（仅限开始前）
     */
    @PostMapping("/update")
    public Result<String> updateVoucher(@RequestBody VoucherDTO dto) {
        Voucher existing = voucherMapper.findById(dto.getId());
        if (existing == null) {
            return Result.fail(MessageConstants.VOUCHER_NOT_FOUND);
        }

        if (LocalDateTime.now().isAfter(existing.getStartTime())) {
            return Result.fail(MessageConstants.VOUCHER_UPDATE_DENIED);
        }

        try {
            // 更新数据库
            existing.setName(dto.getName());
            existing.setStartTime(dto.getStartTime());
            existing.setStock(dto.getStock());
            voucherMapper.update(existing);

            // 同步更新 Redis 库存
            String stockKey = "seckill:stock:" + dto.getId();
            stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(dto.getStock()));

            return Result.ok(MessageConstants.VOUCHER_UPDATED_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MessageConstants.SERVER_ERROR);
        }
    }

    /**
     * 删除优惠券（仅限开始前）
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteVoucher(@PathVariable String id) {
        Voucher existing = voucherMapper.findById(id);
        if (existing == null) {
            return Result.fail(MessageConstants.VOUCHER_NOT_FOUND);
        }

        if (LocalDateTime.now().isAfter(existing.getStartTime())) {
            return Result.fail(MessageConstants.VOUCHER_DELETE_DENIED);
        }

        try {
            // 删除数据库记录
            voucherMapper.delete(id);

            // 删除 Redis 中对应的库存和订单记录
            String stockKey = "seckill:stock:" + id;
            String orderKey = "seckill:order:" + id;
            stringRedisTemplate.delete(Arrays.asList(stockKey, orderKey));

            return Result.ok(MessageConstants.VOUCHER_DELETED_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MessageConstants.SERVER_ERROR);
        }
    }

    /**
     * 查询所有优惠券
     */
    @GetMapping("/list")
    public Result<List<Voucher>> listVouchers() {
        List<Voucher> list = voucherMapper.findAll();
        return Result.ok(list);
    }
}
