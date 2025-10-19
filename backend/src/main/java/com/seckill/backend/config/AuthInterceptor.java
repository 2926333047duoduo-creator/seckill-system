package com.seckill.backend.config;

import com.seckill.backend.entity.User;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, MessageConstants.MISSING_TOKEN);
            return false;
        }

        String token = authHeader.substring(7);
        String account;
        String role;
        String id;

        try {
            account = jwtUtils.getAccount(token);
            role = jwtUtils.getRole(token);
            id = jwtUtils.getUserId(token);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, MessageConstants.INVALID_TOKEN);
            return false;
        }

        String redisKey = "login:token:" + role.toLowerCase() + ":" + id;
        String cachedToken = redisTemplate.opsForValue().get(redisKey);
        if (cachedToken == null || !cachedToken.equals(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, MessageConstants.TOKEN_EXPIRED);
            return false;
        }

        String uri = request.getRequestURI();
        if ("CLIENT".equalsIgnoreCase(role) && !uri.startsWith("/client")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, MessageConstants.FORBIDDEN_ACCESS);
            return false;
        }
        if ("ADMIN".equalsIgnoreCase(role) && !uri.startsWith("/admin")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, MessageConstants.FORBIDDEN_ACCESS);
            return false;
        }


        redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);


        UserContext.setAccount(account);
        UserContext.setRole(role);
        UserContext.setUserId(id);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
