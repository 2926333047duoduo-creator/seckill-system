package com.seckill.backend.service.impl;

import com.seckill.backend.common.MessageConstants;
import com.seckill.backend.common.Result;
import com.seckill.backend.dto.LoginDTO;
import com.seckill.backend.dto.RegisterDTO;
import com.seckill.backend.entity.LoginVO;
import com.seckill.backend.entity.User;
import com.seckill.backend.mapper.UserMapper;
import com.seckill.backend.service.AuthService;
import com.seckill.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
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

    /**
     * User registration (default role: CLIENT)
     */
    @Override
    public Result<String> register(RegisterDTO request) {
        User existing = userMapper.findByAccount(request.getAccount());
        if (existing != null) {
            return Result.fail(MessageConstants.ACCOUNT_ALREADY_EXISTS);
        }

        String encryptedPwd = passwordEncoder.encode(request.getPassword());

        User user = new User();
        String uuid = UUID.randomUUID().toString(); // Generate UUID
        user.setId(uuid);
        user.setAccount(request.getAccount());
        user.setUsername(request.getUsername());
        user.setPassword(encryptedPwd);
        user.setRole("CLIENT");

        userMapper.insert(user);
        return Result.ok(uuid);
    }

    /**
     * User login (multi-role supported)
     */
    @Override
    public Result<LoginVO> login(LoginDTO request) {
        User user = userMapper.findByAccount(request.getAccount());
        if (user == null) {
            return Result.fail(MessageConstants.LOGIN_FAILED);
        }

        if (!user.getRole().equals(request.getRole())) {
            return Result.fail(MessageConstants.ROLE_NOT_MATCH);
        }

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            return Result.fail(MessageConstants.LOGIN_FAILED);
        }

        String token = jwtUtils.createToken(user.getAccount(), user.getRole(), user.getId());

        String redisKey = "login:token:" + user.getRole().toLowerCase() + ":" + user.getId();
        redisTemplate.opsForValue().set(redisKey, token, 30, TimeUnit.MINUTES);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserName(user.getUsername());
        return Result.ok(loginVO);
    }
}
