package com.office_management.message_service.controller;

import com.office_management.message_service.dto.MessageResponseDTO;
import com.office_management.message_service.security.SecurityUtil;
import com.office_management.message_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageStatusController {
    private final MessageService messageService;

    @PutMapping("/{messageId}/delivered")
    public ResponseEntity<MessageResponseDTO> markAsDelivered(@PathVariable Long messageId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(messageService.markAsDelivered(currentUserId, messageId));
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<MessageResponseDTO> markAsRead(@PathVariable Long messageId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(messageService.markAsRead(currentUserId, messageId));
    }
}
