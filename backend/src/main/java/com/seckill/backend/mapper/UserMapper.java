package com.seckill.backend.mapper;

import com.seckill.backend.entity.User;
import org.apache.ibatis.annotations.*;


@Mapper
public interface UserMapper {


    @Select("SELECT * FROM user WHERE account = #{account}")
    User findByAccount(@Param("account") String account);

    @Insert("INSERT INTO user(id, account, username, password, role) " +
            "VALUES(#{id}, #{account}, #{username}, #{password}, #{role})")
    int insert(User user);
}
