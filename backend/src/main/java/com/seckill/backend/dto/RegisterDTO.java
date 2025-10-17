package com.seckill.backend.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String account;   // 登录账号
    private String username;  // 用户昵称/姓名
    private String password;  // 原始密码
}
