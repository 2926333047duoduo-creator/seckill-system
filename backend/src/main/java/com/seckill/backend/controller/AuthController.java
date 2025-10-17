package com.seckill.backend.controller;

import com.seckill.backend.common.Result;
import com.seckill.backend.dto.LoginDTO;
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
    public Result<String> register(@RequestBody RegisterDTO request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO request) {
        return authService.login(request);
    }

}
