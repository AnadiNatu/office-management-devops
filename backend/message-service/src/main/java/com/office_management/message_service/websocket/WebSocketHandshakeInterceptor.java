package com.office_management.message_service.websocket;

import com.office_management.message_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return true;
        }

        String authorization = servletRequest.getServletRequest().getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            // We don't reject the handshake here because browser STOMP clients may provide the JWT
            // during the STOMP CONNECT frame instead.
            return true;
        }

        String token = authorization.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return false;
        }

        Long userId = jwtService.extractUserId(token);
        String email = jwtService.extractEmail(token);
        String role = jwtService.extractRole(token);

        if (userId == null || email == null || role == null) {
            return false;
        }

        attributes.put("userId", userId);
        attributes.put("email", email);
        attributes.put("role", role);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Nothing required after handshake.
    }
}