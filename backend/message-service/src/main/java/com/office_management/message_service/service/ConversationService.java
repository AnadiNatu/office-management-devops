package com.office_management.message_service.service;

import com.office_management.message_service.dto.ConversationCreateRequestDTO;
import com.office_management.message_service.dto.ConversationResponseDTO;
import com.office_management.message_service.dto.ConversationSummaryDTO;
import com.office_management.message_service.dto.PageResponseDTO;

public interface ConversationService {
    ConversationResponseDTO createConversation(Long currentUserId, ConversationCreateRequestDTO request);
    ConversationResponseDTO getConversation(Long currentUserId, Long conversationId);
    PageResponseDTO<ConversationSummaryDTO> getMyConversations(Long currentUserId, int page, int size);
}