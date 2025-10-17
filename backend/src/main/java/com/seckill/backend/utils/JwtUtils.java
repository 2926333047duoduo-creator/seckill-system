package com.seckill.backend.utils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String SECRET = "rjZfTt9YbD1Q2xXp8aNs5eRk7uGv6hJ3wLp4mQ8zVx9nT7rCk1sD5eP2aL6oW9bG";

    /**
     * 生成带角色的 token
     */
    public String createToken(String account, String role) {
        return Jwts.builder()
                .setSubject(account)
                .claim("role", role)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    /**
     * 解析出账号
     */
    public String getAccount(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * 解析出角色
     */
    public String getRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }
}
