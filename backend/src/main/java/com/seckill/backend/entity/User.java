package com.seckill.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String account;    // 登录账号
    private String username;   // 用户昵称
    private String password;   // 加密密码
    private LocalDateTime createTime;
}
