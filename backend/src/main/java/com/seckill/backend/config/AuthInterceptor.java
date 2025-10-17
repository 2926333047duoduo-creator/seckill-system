package com.seckill.backend.config;

import com.seckill.backend.utils.JwtUtils;
import com.seckill.backend.context.UserContext;
import com.seckill.backend.common.MessageConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // HTTP 429 状态码
    private static final int SC_TOO_MANY_REQUESTS = 429;

    // 滑动窗口限流配置：1 秒内最多 10 次
    private static final long LIMIT = 10;
    private static final long WINDOW_SIZE_MS = 1000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        //  校验 JWT 令牌
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少令牌");
            return false;
        }

        String token = authHeader.substring(7);
        String account;
        try {
            account = jwtUtils.getAccount(token);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效令牌");
            return false;
        }

        // 校验 Redis 登录状态
        String redisKey = "login:token:" + account;
        String cachedToken = redisTemplate.opsForValue().get(redisKey);
        if (cachedToken == null || !cachedToken.equals(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "登录状态已过期");
            return false;
        }

        // 滑动窗口限流（Redis ZSet 实现，1 秒内最多 10 次）
        String limitKey = "limit:zset:" + account;
        long now = System.currentTimeMillis();

        // 移除窗口外的请求记录
        redisTemplate.opsForZSet().removeRangeByScore(limitKey, 0, now - WINDOW_SIZE_MS);

        // 添加当前请求
        redisTemplate.opsForZSet().add(limitKey, String.valueOf(now), now);

        // 统计窗口内请求数量
        Long count = redisTemplate.opsForZSet().zCard(limitKey);

        // 设置过期时间（比窗口期略长）
        redisTemplate.expire(limitKey, 2, TimeUnit.SECONDS);

        if (count != null && count > LIMIT) {
            response.sendError(SC_TOO_MANY_REQUESTS, MessageConstants.TOO_MANY_REQUESTS);
            return false;
        }

        // 刷新 Redis 登录过期时间（30 分钟滑动）
        redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);

        // 保存用户信息到 ThreadLocal
        UserContext.setAccount(account);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
