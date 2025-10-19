package com.seckill.backend.controller;

import com.seckill.backend.common.MessageConstants;
import com.seckill.backend.common.Result;
import com.seckill.backend.dto.VoucherDTO;
import com.seckill.backend.entity.Voucher;
import com.seckill.backend.mapper.VoucherMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Create a new voucher
     */
    @PostMapping("/add")
    public Result<String> addVoucher(@RequestBody VoucherDTO dto) {
        try {
            // 1. Validate start time
//            if (dto.getStartTime() == null || dto.getStartTime().isBefore(LocalDateTime.now())) {
//                return Result.fail("Start time cannot be earlier than the current time");
//            }

            // 2. Build entity object
            Voucher voucher = new Voucher();
            String uuid = UUID.randomUUID().toString();
            voucher.setId(uuid);
            voucher.setName(dto.getName());
            voucher.setAmount(dto.getAmount());
            voucher.setTotal(dto.getTotal());
            voucher.setStock(dto.getStock());
            voucher.setStartTime(dto.getStartTime());
            voucher.setCreateTime(LocalDateTime.now());

            // 3. Insert into database
            voucherMapper.insert(voucher);

            // 4. Initialize Redis key for seckill stock
            String stockKey = "seckill:stock:" + uuid;
            stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(dto.getStock()));

            return Result.ok(uuid);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MessageConstants.SERVER_ERROR);
        }
    }

    /**
     * Update voucher information (only allowed before start time)
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
            // Update database record
            existing.setName(dto.getName());
            existing.setStartTime(dto.getStartTime());
            existing.setStock(dto.getStock());
            voucherMapper.update(existing);

            // Sync Redis stock
            String stockKey = "seckill:stock:" + dto.getId();
            stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(dto.getStock()));

            return Result.ok(MessageConstants.VOUCHER_UPDATED_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MessageConstants.SERVER_ERROR);
        }
    }

    /**
     * Delete a voucher (only allowed before start time)
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
            // Delete database record
            voucherMapper.delete(id);

            // Remove corresponding Redis keys (stock and order)
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
     * Retrieve all vouchers
     */
    @GetMapping("/list")
    public Result<List<Voucher>> listVouchers() {
        List<Voucher> list = voucherMapper.findAll();
        return Result.ok(list);
    }
}
