package com.office_management.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // Actuator
                        .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public authentication endpoints
                        .pathMatchers("/api/auth/signup", "/api/auth/login",
                                "/api/auth/refresh", "/api/oauth2/**",
                                "/api/password/**", "/api/password-reset/**",
                                "/api/otp/**").permitAll()

                        // Application APIs
                        .pathMatchers("/api/auth/**", "/api/admin/users/**",
                                "/api/management/**", "/api/profile/**",
                                "/api/notifications/**", "/api/comments/**",
                                "/api/tasks/**", "/api/conversations/**",
                                "/api/messages/**").permitAll()

                        // WebSocket / SockJS
                        .pathMatchers("/ws/**")
                        .permitAll().anyExchange()
                        .authenticated())
                .build();
    }
}