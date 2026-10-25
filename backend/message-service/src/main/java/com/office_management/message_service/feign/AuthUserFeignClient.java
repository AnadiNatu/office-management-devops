package com.office_management.message_service.feign;

import com.office_management.message_service.config.FeignConfig;
import com.office_management.message_service.dto.feign.UserSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", contextId = "messageAuthUserFeignClient", configuration = FeignConfig.class)
public interface AuthUserFeignClient {

    @GetMapping("/api/auth/internal/users/{userId}")
    UserSummaryDTO getUserById(@PathVariable("userId") Long userId);

    @GetMapping("/api/auth/internal/users/{userId}/exists")
    Boolean userExists(@PathVariable("userId") Long userId);
}
