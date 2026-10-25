package com.office_management.message_service.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig   implements WebSocketMessageBrokerConfigurer {

    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;
    private final WebSocketAuthenticationInterceptor webSocketAuthenticationInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Destinations used for messages sent from the server to clients.
        registry.enableSimpleBroker("/topic", "/queue");

        // Destinations used by clients to send messages to application controllers.
        registry.setApplicationDestinationPrefixes("/app");

        // Prefix used for user-specific destinations.
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry
                .addEndpoint("/ws")
                .addInterceptors(webSocketHandshakeInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthenticationInterceptor);
    }
}