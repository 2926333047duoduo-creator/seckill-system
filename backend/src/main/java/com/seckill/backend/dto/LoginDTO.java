package com.seckill.backend.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String account;
    private String password;
    private String role;
}
