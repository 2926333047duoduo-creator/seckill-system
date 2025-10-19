package com.seckill.backend.mapper;

import com.seckill.backend.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户数据访问层
 * 使用 MyBatis 注解方式进行 SQL 映射
 */
@Mapper
public interface UserMapper {

    // 根据账号查询用户
    @Select("SELECT * FROM user WHERE account = #{account}")
    User findByAccount(@Param("account") String account);

    // 插入用户（包含 role）
    @Insert("INSERT INTO user(id, account, username, password, role) " +
            "VALUES(#{id}, #{account}, #{username}, #{password}, #{role})")
    int insert(User user);
}
