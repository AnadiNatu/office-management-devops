package com.office_management.api_gateway.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/api/auth/signup", "/api/auth/login", "/api/auth/refresh");

    public Predicate<ServerHttpRequest> isSecured = request -> OPEN_API_ENDPOINTS.stream()
                    .noneMatch(openEndpoint -> request.getURI().getPath().equals(openEndpoint));
}