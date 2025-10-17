package com.seckill.backend.utils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtils {


    private static final String SECRET = "rjZfTt9YbD1Q2xXp8aNs5eRk7uGv6hJ3wLp4mQ8zVx9nT7rCk1sD5eP2aL6oW9bG";

    private static final long EXPIRE_TIME = 30 * 60 * 1000; // 30分钟

    // 生成 token
    public String createToken(String account) {
        return Jwts.builder()
                .setSubject(account)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // 解析 token
    public String getAccount(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 判断是否过期
    public boolean isExpired(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().before(new Date());
    }
}
