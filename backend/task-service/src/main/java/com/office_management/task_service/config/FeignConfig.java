package com.office_management.task_service.config;

import com.office_management.task_service.exception.errors.FeignServiceException;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Value("${internal.security.service-key}")
    private String serviceKey;

    @Bean
    public RequestInterceptor internalServiceRequestInterceptor() {
        return requestTemplate -> {requestTemplate.header("X-Internal-Service-Key", serviceKey);
        };
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return (methodKey, response) -> {
            String message = String.format("Auth Service request failed. Method: %s, Status: %d",methodKey, response.status());
            return new FeignServiceException(message);
        };
    }
}