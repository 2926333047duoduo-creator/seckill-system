package com.seckill.backend.service;

import com.seckill.backend.dto.RegisterDTO;

public interface AuthService {
    void register(RegisterDTO request);
}
