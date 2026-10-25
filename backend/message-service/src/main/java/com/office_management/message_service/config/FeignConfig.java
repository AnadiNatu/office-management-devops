package com.office_management.message_service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Value("${internal.security.service-key}")
    private String internalServiceKey;

    @Bean
    public RequestInterceptor internalServiceKeyInterceptor() {

        return requestTemplate -> {
            requestTemplate.header("X-Internal-Service-Key", internalServiceKey);
        };
    }
}