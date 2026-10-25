package com.office_management.notification_service.websocket;

import com.office_management.notification_service.security.CustomUserDetails;
import com.office_management.notification_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message , MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.COMMIT.equals(accessor.getCommand())){
            String authorization  = accessor.getFirstNativeHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer ")){
                return null;
            }

            String token = authorization.substring(7);
            try
            {
                if (!jwtService.isTokenValid(token)){
                    return null;
                }
                Long userId = jwtService.extractUserId(token);
                String email = jwtService.extractEmail(token);
                String role = jwtService.extractRole(token);

                if (userId == null || email == null || role == null) {
                    return null;
                }

                CustomUserDetails userDetails = new CustomUserDetails(userId, email, role);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                accessor.setUser(authentication);
            }catch (Exception ex){
                return null;
            }
        }
        return message;
    }
}
