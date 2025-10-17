package com.seckill.backend.service.impl;

import com.seckill.backend.common.MessageConstants;
import com.seckill.backend.common.Result;
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
    public Result<String> register(RegisterDTO request) {
        User existing = userMapper.findByAccount(request.getAccount());
        if (existing != null) {
            return Result.fail("账号已存在，请更换一个");
        }
        String encryptedPwd = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setAccount(request.getAccount());
        user.setUsername(request.getUsername());
        user.setPassword(encryptedPwd);

        userMapper.insert(user);
        return Result.ok(MessageConstants.REGISTER_SUCCESS);
    }

}
