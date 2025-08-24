package com.eardream.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 설정
 * 포트원 API 호출을 위한 HTTP 클라이언트 설정
 */
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // 연결 타임아웃 설정 (10초)
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(30000);

        return new RestTemplate(factory);
    }
}