package com.seckill.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private String id;
    private String account;
    private String username;
    private String password;
    private LocalDateTime createTime;
    private String role;
}
