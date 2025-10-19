package com.seckill.backend.service;

import com.seckill.backend.common.Result;
import com.seckill.backend.dto.LoginDTO;
import com.seckill.backend.dto.RegisterDTO;
import com.seckill.backend.entity.LoginVO;

public interface AuthService {
    Result<String> register(RegisterDTO request);
    Result<LoginVO> login(LoginDTO request);
}
