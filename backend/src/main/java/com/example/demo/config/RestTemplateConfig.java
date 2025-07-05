package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean("defaultRestTemplate")
    RestTemplate defaultRestTemplate() {
        // 따로 설정할 Config 없음
        return new RestTemplate();
    }
}