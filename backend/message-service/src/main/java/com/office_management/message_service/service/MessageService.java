package com.office_management.message_service.service;

import com.office_management.message_service.dto.MessageCreateRequestDTO;
import com.office_management.message_service.dto.MessageResponseDTO;
import com.office_management.message_service.dto.PageResponseDTO;

public interface MessageService {
    MessageResponseDTO sendMessage(Long currentUserId, Long conversationId, MessageCreateRequestDTO request);
    PageResponseDTO<MessageResponseDTO> getMessages(Long currentUserId, Long conversationId, int page, int size);
    MessageResponseDTO markAsDelivered(Long currentUserId, Long messageId);
    MessageResponseDTO markAsRead(Long currentUserId, Long messageId);
}