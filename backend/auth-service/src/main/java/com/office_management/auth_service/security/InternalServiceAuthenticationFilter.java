package com.office_management.auth_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class InternalServiceAuthenticationFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PREFIX = "/api/auth/internal/";
    private static final String INTERNAL_HEADER = "X-Internal-Service-Key";
    private static final String INTERNAL_AUTHORITY = "INTERNAL_SERVICE";

    @Value("${internal.security.service-key}")
    private String serviceKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!path.startsWith(INTERNAL_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedKey = request.getHeader(INTERNAL_HEADER);

        if (providedKey == null ||  serviceKey == null ||!providedKey.equals(serviceKey)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Internal service authentication required\"}");
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("internal-service" , null , List.of(new SimpleGrantedAuthority(INTERNAL_AUTHORITY)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}