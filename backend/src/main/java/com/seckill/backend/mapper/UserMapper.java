package com.seckill.backend.mapper;

import com.seckill.backend.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户数据访问层
 * 使用 MyBatis 注解方式进行 SQL 映射
 */
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE account = #{account}")
    User findByAccount(@Param("account") String account);

    @Insert("INSERT INTO user(account, username, password) VALUES(#{account}, #{username}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}
