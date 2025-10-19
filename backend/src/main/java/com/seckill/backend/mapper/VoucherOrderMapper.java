package com.seckill.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.backend.entity.VoucherOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface VoucherOrderMapper {

    // Idempotency check: verify whether the user has already placed an order
    @Select("SELECT COUNT(*) FROM voucher_order WHERE user_id = #{userId} AND voucher_id = #{voucherId}")
    int countByUserAndVoucher(@Param("userId") String userId, @Param("voucherId") String voucherId);

    // Decrease stock using optimistic locking
    @Update("UPDATE voucher SET stock = stock - 1 WHERE id = #{voucherId} AND stock > 0")
    int decrementStock(@Param("voucherId") String voucherId);

    // Insert a new order (status defaults to PENDING, order_time auto-filled)
    @Insert("INSERT INTO voucher_order (id, user_id, voucher_id) VALUES (#{id}, #{userId}, #{voucherId})")
    int insertOrder(VoucherOrder order);
}
