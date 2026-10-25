package com.office_management.comment_service.config;

import com.office_management.comment_service.feign.FeignErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    @Value("${internal.security.service-key}")
    private String serviceKey;

    @Bean
    public RequestInterceptor internalServiceRequestInterceptor() {
        return requestTemplate -> {
            // Every internal service-to-service request carries the shared internal service key
            requestTemplate.header("X-Internal-Service-Key", serviceKey);

            /*
             * Forward the user's JWT when comment-service is
             * processing a request that originated from the frontend.
             *
             * This is required when calling services such as
             * task-service whose endpoints require user authentication.
             */
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            String authorization = request.getHeader("Authorization");

            if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
                requestTemplate.header("Authorization", authorization);
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
