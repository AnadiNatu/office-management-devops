package com.office_management.auth_service.config;

import com.office_management.auth_service.feign.FeignErrorDecoder;
import com.office_management.auth_service.feign.FeignInterceptor;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor internalFeignInterceptor(FeignInterceptor interceptor) {
        return interceptor;
    }

//    @Bean
//    public ErrorDecoder feignErrorDecoder() {
//        return new FeignErrorDecoder();
//    }
}