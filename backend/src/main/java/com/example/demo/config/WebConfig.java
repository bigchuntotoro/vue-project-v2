package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // /api/로 시작하는 모든 요청에 대해
                .allowedOrigins("http://localhost:5173") // Vue 기본 포트 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("*");
    }
}