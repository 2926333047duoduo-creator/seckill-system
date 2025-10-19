package com.seckill.backend.config;

import com.seckill.backend.common.MessageConstants;
import com.seckill.backend.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // HTTP 429 状态码
    private static final int SC_TOO_MANY_REQUESTS = 429;

    // 限流配置：1 秒最多 10 次
    private static final long LIMIT = 10;
    private static final long WINDOW_SIZE_MS = 1000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException, IOException {
        String account = UserContext.getAccount();
        String role = UserContext.getRole();

        if (account == null || role == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, MessageConstants.MISSING_TOKEN);
            return false;
        }

        String limitKey = "limit:zset:" + role.toLowerCase() + ":" + UserContext.getUserId();
        long now = System.currentTimeMillis();

        // 移除窗口外请求
        redisTemplate.opsForZSet().removeRangeByScore(limitKey, 0, now - WINDOW_SIZE_MS);
        // 添加当前请求
        redisTemplate.opsForZSet().add(limitKey, String.valueOf(now), now);
        // 获取窗口内数量
        Long count = redisTemplate.opsForZSet().zCard(limitKey);
        // 设置过期时间
        redisTemplate.expire(limitKey, 2, TimeUnit.SECONDS);

        if (count != null && count > LIMIT) {
            response.sendError(SC_TOO_MANY_REQUESTS, MessageConstants.TOO_MANY_REQUESTS);
            return false;
        }

        return true;
    }
}
