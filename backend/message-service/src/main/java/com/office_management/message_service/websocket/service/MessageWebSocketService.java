package com.office_management.message_service.websocket.service;

import com.office_management.message_service.dto.MessageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private static final String MESSAGE_DESTINATION = "/queue/messages";
    private static final String STATUS_DESTINATION = "/queue/message-status";


    /**
     * Sends a message to one specific authenticated user.
     *
     * The WebSocket principal name must correspond to the
     * user ID used here.
     */
    public void sendMessage(Long recipientUserId, MessageResponseDTO message) {

        if (recipientUserId == null) {
            log.warn("Cannot send WebSocket message: recipient user ID is null");
            return;
        }

        if (message == null) {
            log.warn("Cannot send WebSocket message: message is null");
            return;
        }

        try {
            messagingTemplate.convertAndSendToUser(recipientUserId.toString(), MESSAGE_DESTINATION, message);
            log.debug("WebSocket message sent to user {}", recipientUserId);

        } catch (Exception ex) {

            /*
             * WebSocket delivery failure must not roll back
             * successful database persistence.
             */
            log.error("Failed to deliver WebSocket message to user {}", recipientUserId, ex);
        }
    }

    /**
     * Sends a message-status update to one specific user.
     *
     * This is normally used to notify the original sender that
     * their message has been delivered or read.
     */
    public void sendStatusUpdate(Long recipientUserId, MessageResponseDTO message) {

        if (recipientUserId == null) {
            log.warn("Cannot send WebSocket status update: recipient user ID is null");
            return;
        }

        if (message == null) {
            log.warn("Cannot send WebSocket status update: message is null");
            return;
        }

        try {
            messagingTemplate.convertAndSendToUser(recipientUserId.toString(), STATUS_DESTINATION, message);

            log.debug("Message status update sent to user {}",recipientUserId);
        } catch (Exception ex) {
            log.error("Failed to send WebSocket status update to user {}", recipientUserId, ex);
        }
    }
}