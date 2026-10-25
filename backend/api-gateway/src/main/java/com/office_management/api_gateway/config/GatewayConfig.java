package com.office_management.api_gateway.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // =========================================================
                // AUTH SERVICE
                // =========================================================

                .route("auth-service", route -> route
                        .path(
                                "/api/auth/**",
                                "/api/admin/users/**",
                                "/api/management/**",
                                "/api/oauth2/**",
                                "/api/password/**",
                                "/api/password-reset/**",
                                "/api/otp/**",
                                "/api/profile/**"
                        )
                        .uri("lb://auth-service"))

                // =========================================================
                // COMMENT SERVICE
                // =========================================================

                .route("comment-service", route -> route
                        .path("/api/comments/**")
                        .uri("lb://comment-service"))

                // =========================================================
                // TASK SERVICE
                // =========================================================

                .route("task-service", route -> route
                        .path("/api/tasks/**")
                        .uri("lb://task-service"))

                // =========================================================
                // NOTIFICATION SERVICE
                // =========================================================

                .route("notification-service", route -> route
                        .path("/api/notifications/**")
                        .uri("lb://notification-service"))

                // =========================================================
                // MESSAGE SERVICE - CONVERSATIONS
                // =========================================================

                .route("message-service-conversations", route -> route
                        .path("/api/conversations/**")
                        .uri("lb://message-service"))

                // =========================================================
                // MESSAGE SERVICE - REST MESSAGE STATUS
                // =========================================================

                .route("message-service-messages", route -> route
                        .path("/api/messages/**")
                        .uri("lb://message-service"))

                // =========================================================
                // MESSAGE SERVICE - SOCKJS HTTP FALLBACK
                // =========================================================
                //
                // Important for SockJS:
                // /ws/info
                // /ws/{server}/{session}/xhr
                // etc.
                //

                .route("message-service-websocket-http", route -> route
                        .path("/ws/**")
                        .and()
                        .not(r -> r.header("Upgrade" , "webSocket"))
                        .uri("lb://message-service"))

                // =========================================================
                // MESSAGE SERVICE - ACTUAL WEBSOCKET
                // =========================================================
                //
                // Used for:
                // ws://localhost:8080/ws/...
                //

                .route("message-service-websocket", route -> route
                        .path("/ws/**")
                        .and()
                        .header("Upgrade" , "webSocket")
                        .uri("ws://message-service"))
                .build();
    }
}