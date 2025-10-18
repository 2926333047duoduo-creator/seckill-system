package com.seckill.backend.utils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String SECRET = "rjZfTt9YbD1Q2xXp8aNs5eRk7uGv6hJ3wLp4mQ8zVx9nT7rCk1sD5eP2aL6oW9bG";

    /**
     * 生成包含 account、role、userId 的 token
     */
    public String createToken(String account, String role, String userId) {
        return Jwts.builder()
                .setSubject(account)                        // "sub" = account
                .claim("role", role)                        // 自定义字段 role
                .claim("userId", userId)                    // 自定义字段 userId（UUID 字符串）
                .setIssuedAt(new Date())                    // 签发时间
                .signWith(SignatureAlgorithm.HS256, SECRET) // 签名算法
                .compact();
    }

    /**
     * 解析出账号（account）
     */
    public String getAccount(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 解析出角色
     */
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * 解析出用户ID（UUID）
     */
    public String getUserId(String token) {
        return parseClaims(token).get("userId", String.class);
    }

    /**
     * 通用 Claims 解析方法
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
