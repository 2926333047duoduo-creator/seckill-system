package com.seckill.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许前端的域名（可以改成你的前端地址）
        config.addAllowedOriginPattern("*"); // 可用 "*" 或具体 "http://localhost:5173"
        config.setAllowCredentials(true); // 允许携带 Cookie
        config.addAllowedHeader("*");     // 允许的请求头
        config.addAllowedMethod("*");     // 允许的请求方法（GET, POST, PUT, DELETE）
        config.setMaxAge(3600L);          // 预检请求的缓存时间（1小时）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
