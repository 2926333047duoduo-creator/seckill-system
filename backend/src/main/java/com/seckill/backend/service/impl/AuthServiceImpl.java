package com.seckill.backend.service.impl;

import com.seckill.backend.common.MessageConstants;
import com.seckill.backend.common.Result;
import com.seckill.backend.dto.RegisterDTO;
import com.seckill.backend.dto.LoginDTO;
import com.seckill.backend.entity.User;
import com.seckill.backend.mapper.UserMapper;
import com.seckill.backend.service.AuthService;
import com.seckill.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    // 注册逻辑
    @Override
    public Result<String> register(RegisterDTO request) {
        User existing = userMapper.findByAccount(request.getAccount());
        if (existing != null) {
            return Result.fail(MessageConstants.ACCOUNT_ALREADY_EXISTS);
        }

        String encryptedPwd = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setAccount(request.getAccount());
        user.setUsername(request.getUsername());
        user.setPassword(encryptedPwd);

        userMapper.insert(user);
        return Result.ok(MessageConstants.REGISTER_SUCCESS);
    }

    // 登录逻辑
    @Override
    public Result<String> login(LoginDTO request) {
        User user = userMapper.findByAccount(request.getAccount());
        if (user == null) {
            return Result.fail(MessageConstants.LOGIN_FAILED);
        }

        // 验证密码
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            return Result.fail(MessageConstants.LOGIN_FAILED);
        }

        // 生成JWT令牌
        String token = jwtUtils.createToken(user.getAccount());

        // 保存到Redis（半小时过期）
        String redisKey = "login:token:" + user.getAccount();
        redisTemplate.opsForValue().set(redisKey, token, 30, TimeUnit.MINUTES);

        return Result.ok(token);
    }
}
