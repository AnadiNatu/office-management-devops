package com.office_management.auth_service.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeignInterceptor implements RequestInterceptor {
    @Value("${internal.security.service-key}")
    private String serviceKey;

    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Internal-Service-Key", serviceKey);
    }
}