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

    // HTTP 429 status code
    private static final int SC_TOO_MANY_REQUESTS = 429;

    // Rate limiting configuration: max 10 requests per second
    private static final long LIMIT = 10;
    private static final long WINDOW_SIZE_MS = 1000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String account = UserContext.getAccount();
        String role = UserContext.getRole();

        if (account == null || role == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, MessageConstants.MISSING_TOKEN);
            return false;
        }

        String limitKey = "limit:zset:" + role.toLowerCase() + ":" + UserContext.getUserId();
        long now = System.currentTimeMillis();


        redisTemplate.opsForZSet().removeRangeByScore(limitKey, 0, now - WINDOW_SIZE_MS);

        redisTemplate.opsForZSet().add(limitKey, String.valueOf(now), now);

        Long count = redisTemplate.opsForZSet().zCard(limitKey);

        redisTemplate.expire(limitKey, 2, TimeUnit.SECONDS);

        if (count != null && count > LIMIT) {
            response.sendError(SC_TOO_MANY_REQUESTS, MessageConstants.TOO_MANY_REQUESTS);
            return false;
        }

        return true;
    }
}
