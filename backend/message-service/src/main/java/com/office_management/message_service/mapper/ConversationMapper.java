package com.office_management.message_service.mapper;

import com.office_management.message_service.dto.ConversationResponseDTO;
import com.office_management.message_service.dto.ConversationSummaryDTO;
import com.office_management.message_service.dto.PageResponseDTO;
import com.office_management.message_service.entity.Conversation;
import com.office_management.message_service.entity.ConversationParticipant;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConversationMapper {
    public ConversationResponseDTO toResponseDTO(Conversation conversation, List<ConversationParticipant> participants) {

        if (conversation == null) {
            return null;
        }

        List<Long> participantUserIds = participants == null
                        ? List.of()
                        : participants.stream()
                          .map(ConversationParticipant::getUserId)
                          .toList();

        return ConversationResponseDTO.builder()
                .id(conversation.getId())
                .type(conversation.getType())
                .participantUserIds(participantUserIds)
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    public ConversationSummaryDTO toSummaryDTO(Conversation conversation) {

        if (conversation == null) {
            return null;
        }

        return ConversationSummaryDTO.builder()
                .id(conversation.getId())
                .type(conversation.getType())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    public PageResponseDTO<ConversationSummaryDTO> toPageResponseDTO(Page<Conversation> page) {

        return PageResponseDTO.<ConversationSummaryDTO>builder()
                .content(page.getContent().stream()
                                .map(this::toSummaryDTO).toList())
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