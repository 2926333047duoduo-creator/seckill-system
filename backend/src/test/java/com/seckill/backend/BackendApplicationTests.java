package com.seckill.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class BackendApplicationTests {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    void contextLoads() {
        try {
            redisTemplate.opsForValue().set("test:key", "hello redis");
            String value = redisTemplate.opsForValue().get("test:key");
            System.out.println("✅ Redis 连接成功，读取到值：" + value);
        } catch (Exception e) {
            System.err.println("❌ Redis 连接失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

}
