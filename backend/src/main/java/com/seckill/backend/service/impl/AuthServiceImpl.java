package com.seckill.backend.service.impl;

import com.seckill.backend.common.ErrorMessages;
import com.seckill.backend.dto.RegisterDTO;
import com.seckill.backend.entity.User;
import com.seckill.backend.mapper.UserMapper;
import com.seckill.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterDTO request) {
        // 检查账号是否重复
        User existing = userMapper.findByAccount(request.getAccount());
        if (existing != null) {
            throw new RuntimeException(ErrorMessages.ACCOUNT_ALREADY_EXISTS);
        }

        // 构建用户对象并加密密码
        User user = new User();
        user.setAccount(request.getAccount());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // BCrypt 加密

        userMapper.insert(user);
    }
}
