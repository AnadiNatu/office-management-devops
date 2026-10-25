package com.office_management.message_service.mapper;

import com.office_management.message_service.dto.MessageResponseDTO;
import com.office_management.message_service.dto.MessageSummaryDTO;
import com.office_management.message_service.dto.PageResponseDTO;
import com.office_management.message_service.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageResponseDTO toResponseDTO(Message message) {

        if (message == null) {
            return null;
        }

        return MessageResponseDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderUserId(message.getSenderUserId())
                .recipientUserId(message.getRecipientUserId())
                .content(message.getContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .deliveredAt(message.getDeliveredAt())
                .readAt(message.getReadAt())
                .build();
    }

    public MessageSummaryDTO toSummaryDTO(Message message) {

        if (message == null) {
            return null;
        }

        return MessageSummaryDTO.builder()
                .id(message.getId())
                .senderUserId(message.getSenderUserId())
                .content(message.getContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public PageResponseDTO<MessageResponseDTO> toPageResponseDTO(Page<Message> page) {

        return PageResponseDTO.<MessageResponseDTO>builder()
                .content(page.getContent().stream()
                                .map(this::toResponseDTO).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}