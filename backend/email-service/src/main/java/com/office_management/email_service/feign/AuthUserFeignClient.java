package com.office_management.email_service.feign;

import com.office_management.email_service.config.FeignConfig;
import com.office_management.email_service.dto.UserEmailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service" , contextId = "emailAuthUserFeignClient" , configuration = FeignConfig.class)
public interface AuthUserFeignClient {

    @GetMapping("/api/auth/internal/users/{userId}")
    UserEmailDTO getUserById(@PathVariable("userId") Long userId);

    @GetMapping("/api/auth/internal/users/{userId}/exists")
    Boolean userExists(@PathVariable("userId") Long userId);
}