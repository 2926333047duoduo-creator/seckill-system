package com.seckill.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.backend.entity.VoucherOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface VoucherOrderMapper {

    // 幂等判断：查询是否已下过单
    @Select("SELECT COUNT(*) FROM voucher_order WHERE user_id = #{userId} AND voucher_id = #{voucherId}")
    int countByUserAndVoucher(@Param("userId") String userId, @Param("voucherId") String voucherId);

    // 扣减库存（乐观锁）
    @Update("UPDATE voucher SET stock = stock - 1 WHERE id = #{voucherId} AND stock > 0")
    int decrementStock(@Param("voucherId") String voucherId);

    // 插入订单（status 默认 PENDING，order_time 自动填充）
    @Insert("INSERT INTO voucher_order (id, user_id, voucher_id) VALUES (#{id}, #{userId}, #{voucherId})")
    int insertOrder(VoucherOrder order);
}
