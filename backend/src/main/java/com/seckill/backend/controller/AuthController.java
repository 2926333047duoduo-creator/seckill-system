package com.seckill.backend.controller;

import com.seckill.backend.dto.RegisterDTO;
import com.seckill.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterDTO request) {
        authService.register(request);
        return "注册成功";
    }
}
