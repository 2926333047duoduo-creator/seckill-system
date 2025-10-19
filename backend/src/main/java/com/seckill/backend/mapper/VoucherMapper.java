package com.seckill.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.backend.entity.Voucher;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VoucherMapper extends BaseMapper<Voucher> {

    @Insert("INSERT INTO voucher(id, name, amount, total, stock, start_time, create_time) " +
            "VALUES(#{id}, #{name}, #{amount}, #{total}, #{stock}, #{startTime}, #{createTime})")
    int insert(Voucher voucher);

    @Select("SELECT * FROM voucher WHERE id = #{id}")
    Voucher findById(@Param("id") String id);

    @Select("SELECT * FROM voucher ORDER BY create_time DESC")
    List<Voucher> findAll();

    @Update("UPDATE voucher SET name = #{name}, start_time = #{startTime} WHERE id = #{id}")
    int update(Voucher voucher);

    @Delete("DELETE FROM voucher WHERE id = #{id}")
    int delete(@Param("id") String id);
}
