package com.eardream.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정
 * CORS, 정적 리소스 핸들링 등 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    /**
     * CORS 설정
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:3000",           // React 개발 서버
                    "http://localhost:5173",           // Vite 개발 서버
                    "https://eardream.com",           // 프로덕션 도메인
                    "https://www.eardream.com"        // www 도메인
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    /**
     * 정적 리소스 핸들러 설정
     * 업로드된 파일 서빙
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 이미지 파일 서빙
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + uploadPath + "/images/")
                .setCachePeriod(3600);
        
        // 업로드된 PDF 파일 서빙
        registry.addResourceHandler("/uploads/pdfs/**")
                .addResourceLocations("file:" + uploadPath + "/pdfs/")
                .setCachePeriod(3600);
    }
}