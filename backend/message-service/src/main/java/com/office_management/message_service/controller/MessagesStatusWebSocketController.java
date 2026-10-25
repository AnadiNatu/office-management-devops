package com.office_management.message_service.controller;

import com.office_management.message_service.dto.MessageResponseDTO;
import com.office_management.message_service.dto.MessageStatusRequestDTO;
import com.office_management.message_service.security.CustomUserDetails;
import com.office_management.message_service.service.MessageService;
import com.office_management.message_service.websocket.service.MessageWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessagesStatusWebSocketController {


    private final MessageService messageService;
    private final MessageWebSocketService messageWebSocketService;

    @MessageMapping("/messages.delivered")
    public void markAsDelivered(@Payload MessageStatusRequestDTO request, StompHeaderAccessor accessor) {

//        CustomUserDetails userDetails = getUserDetails(accessor);
//        messageService.markAsDelivered(userDetails.getUserId(),request.getMessageId());

        CustomUserDetails userDetails =getUserDetails(accessor);
        MessageResponseDTO response = messageService.markAsDelivered(userDetails.getUserId(), request.getMessageId());
        messageWebSocketService.sendStatusUpdate(response.getSenderUserId(), response);
    }

    @MessageMapping("/messages.read")
    public void markAsRead(@Payload MessageStatusRequestDTO request, StompHeaderAccessor accessor) {

//        CustomUserDetails userDetails = getUserDetails(accessor);
//        messageService.markAsRead(userDetails.getUserId(), request.getMessageId());
        CustomUserDetails userDetails = getUserDetails(accessor);

        MessageResponseDTO response = messageService.markAsRead(userDetails.getUserId(), request.getMessageId());

        messageWebSocketService.sendStatusUpdate(response.getSenderUserId(), response);

    }

    private CustomUserDetails getUserDetails(StompHeaderAccessor accessor) {

        Principal principal = accessor.getUser();

        if (principal == null) {
            throw new IllegalStateException("WebSocket user is not authenticated");
        }

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Invalid WebSocket user principal");
        }

        return userDetails;
    }
}