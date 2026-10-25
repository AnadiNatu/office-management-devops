package com.office_management.notification_service.websocket;

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
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;
    private final WebSocketAuthenticationInterceptor webSocketAuthenticationInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        // Messages sent to /topic or /queue are handled by Spring's message broker.
        registry.enableSimpleBroker("/topic" , "/queue");

        //  Messages sent by clients to application destinations will start with /app.
        registry.setApplicationDestinationPrefixes("/app");

        // User specific destination use /user
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthenticationInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry
                .addEndpoint("/ws")
                .addInterceptors(webSocketHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
