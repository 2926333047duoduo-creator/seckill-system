package com.seckill.backend.service;

import com.seckill.backend.common.Result;
import com.seckill.backend.dto.RegisterDTO;

public interface AuthService {
    Result<String> register(RegisterDTO request);
}
