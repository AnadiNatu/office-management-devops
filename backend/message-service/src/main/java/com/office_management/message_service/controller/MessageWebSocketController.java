package com.office_management.message_service.controller;

import com.office_management.message_service.dto.MessageCreateRequestDTO;
import com.office_management.message_service.dto.MessageResponseDTO;
import com.office_management.message_service.dto.WebSocketMessageRequestDTO;
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
public class MessageWebSocketController {
    private final MessageService messageService;
    private final MessageWebSocketService messageWebSocketService;

    @MessageMapping("/messages.send")
    public void sendMessage(@Payload WebSocketMessageRequestDTO request, StompHeaderAccessor accessor) {

        CustomUserDetails userDetails = getUserDetails(accessor);

        String conversationIdHeader = accessor.getFirstNativeHeader("conversationId");

        if (conversationIdHeader == null || conversationIdHeader.isBlank()) {
            throw new IllegalArgumentException("Conversation ID is required");
        }
        Long conversationId;

        try {
            conversationId = Long.parseLong(conversationIdHeader);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Conversation ID must be numeric");
        }

        Long currentUserId = userDetails.getUserId();
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Message content is required");
        }

        MessageCreateRequestDTO messageRequest = MessageCreateRequestDTO.builder()
                        .content(request.getContent().trim())
                        .build();

        MessageResponseDTO response = messageService.sendMessage(currentUserId, conversationId, messageRequest);
        messageWebSocketService.sendMessage(response.getRecipientUserId(), response);

        /*
         * Also send the persisted message back to the sender.
         *
         * This is important because the frontend considers the
         * WebSocket publish successful and therefore does not use
         * the REST fallback. Without this notification, the sender
         * would not receive the persisted message in real time.
         */
        messageWebSocketService.sendMessage(response.getSenderUserId(), response);
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