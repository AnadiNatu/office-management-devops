package com.office_management.auth_service.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignLoggingConfig {

    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.BASIC;
    }

}