package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 1. 이미지 및 첨부파일 정적 경로 매핑
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                // ⚠️ 마지막에 반드시 '/'가 들어가야 합니다!
                // C:/upload/ 경로에 파일이 실제로 들어있는지 꼭 확인하세요.
                .addResourceLocations("file:///C:/upload/");
    }

    // 2. Vue(5173 또는 8081)에서 이미지/API 요청을 허용하도록 CORS 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:8080", "http://localhost:3000") // 본인의 Vue 포트번호
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}