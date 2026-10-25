package com.office_management.notification_service.websocket;

import com.office_management.notification_service.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long recipientUserId , NotificationResponseDTO notification){
        if (recipientUserId == null || notification == null){
            return;
        }

        String destination = "/queue/notification";
        log.info("Sending real-time notification. userId={} , notification={}" , recipientUserId , notification.getId());

        messagingTemplate.convertAndSendToUser(recipientUserId.toString() , destination , notification);
    }
}
