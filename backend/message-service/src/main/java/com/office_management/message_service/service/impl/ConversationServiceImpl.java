package com.office_management.message_service.service.impl;

import com.office_management.message_service.dto.ConversationCreateRequestDTO;
import com.office_management.message_service.dto.ConversationResponseDTO;
import com.office_management.message_service.dto.ConversationSummaryDTO;
import com.office_management.message_service.dto.PageResponseDTO;
import com.office_management.message_service.entity.Conversation;
import com.office_management.message_service.entity.ConversationParticipant;
import com.office_management.message_service.enums.ConversationType;
import com.office_management.message_service.exception.errors.ConversationAlreadyExistsException;
import com.office_management.message_service.exception.errors.InvalidConversationException;
import com.office_management.message_service.exception.errors.ResourceNotFoundException;
import com.office_management.message_service.mapper.ConversationMapper;
import com.office_management.message_service.repository.ConversationParticipantRepository;
import com.office_management.message_service.repository.ConversationRepository;
import com.office_management.message_service.service.AuthUserService;
import com.office_management.message_service.service.ConversationService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

        private final ConversationRepository conversationRepository;
        private final ConversationParticipantRepository participantRepository;
        private final ConversationMapper conversationMapper;
        private final AuthUserService authUserService;

        @Override
        @Transactional
        public ConversationResponseDTO createConversation (Long currentUserId, ConversationCreateRequestDTO request){

            Long participantUserId = request.getParticipantUserId();

            if (currentUserId == null || currentUserId <= 0) {
                throw new IllegalArgumentException("Current user ID is invalid");
            }

            if (participantUserId == null || participantUserId <= 0) {
                throw new IllegalArgumentException("Participant user ID is invalid");
            }

            if (currentUserId.equals(participantUserId)) {
                throw new InvalidConversationException("You cannot create a conversation with yourself");
            }

            // Validate the target user through Auth Service.
            authUserService.validateUser(participantUserId);

            // Check whether a one-to-one conversation already exists between these two users.

            Conversation existingConversation = findExistingOneToOneConversation(currentUserId, participantUserId);

            if (existingConversation != null) {
                throw new ConversationAlreadyExistsException("A conversation already exists between these users");
            }

            // Create the conversation
            Conversation conversation = Conversation.builder()
                            .type(ConversationType.ONE_TO_ONE)
                            .build();

            conversation = conversationRepository.save(conversation);

            // Add current user.
            ConversationParticipant currentUserParticipant = ConversationParticipant.builder()
                            .conversationId(conversation.getId())
                            .userId(currentUserId)
                            .build();

            // Add target user.
            ConversationParticipant targetUserParticipant = ConversationParticipant.builder()
                            .conversationId(conversation.getId())
                            .userId(participantUserId)
                            .build();

            participantRepository.save(currentUserParticipant);
            participantRepository.save(targetUserParticipant);

            List<ConversationParticipant> participants = List.of(currentUserParticipant, targetUserParticipant);
            return conversationMapper.toResponseDTO(conversation, participants);
        }

        @Override
        @Transactional(readOnly = true)
        public ConversationResponseDTO getConversation (Long currentUserId, Long conversationId){

            verifyParticipant(conversationId, currentUserId);

            Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new ResourceNotFoundException("Conversation not found with ID: " + conversationId));
            List<ConversationParticipant> participants = participantRepository.findByConversationId(conversationId);
            return conversationMapper.toResponseDTO(conversation, participants);
        }

        @Override
        @Transactional(readOnly = true)
        public PageResponseDTO<ConversationSummaryDTO> getMyConversations(Long currentUserId, int page, int size){

            validatePagination(page, size);

            List<ConversationParticipant> memberships = participantRepository.findByUserId(currentUserId);

            List<Long> conversationIds = memberships.stream()
                            .map(ConversationParticipant::getConversationId)
                            .toList();

            if (conversationIds.isEmpty()) {

                return PageResponseDTO
                        .<ConversationSummaryDTO>builder()
                        .content(List.of())
                        .page(page)
                        .size(size)
                        .totalElements(0)
                        .totalPages(0)
                        .first(true)
                        .last(true)
                        .empty(true)
                        .build();
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Conversation> conversations = conversationRepository.findByIdInOrderByUpdatedAtDesc(conversationIds, pageable);

            return conversationMapper.toPageResponseDTO(conversations);
        }

        private Conversation findExistingOneToOneConversation (Long userIdOne, Long userIdTwo){

            List<ConversationParticipant> firstUserConversations = participantRepository.findByUserId(userIdOne);

            for (ConversationParticipant participant : firstUserConversations) {

                Long conversationId = participant.getConversationId();
                Conversation conversation = conversationRepository.findByIdAndType(conversationId, ConversationType.ONE_TO_ONE).orElse(null);

                if (conversation == null) {
                    continue;
                }
                boolean secondUserParticipates = participantRepository.existsByConversationIdAndUserId(conversationId, userIdTwo);
                if (secondUserParticipates) {
                    return conversation;
                }
            }
            return null;
        }

        private void verifyParticipant (Long conversationId, Long userId){

            if (conversationId == null || conversationId <= 0) {
                throw new IllegalArgumentException("Conversation ID must be positive");
            }

            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID must be positive");
            }

            boolean participant = participantRepository.existsByConversationIdAndUserId(conversationId, userId);

            if (!participant) {
                throw new InvalidConversationException("You are not a participant of this conversation");
            }
        }

        private void validatePagination ( int page, int size){

            if (page < 0) {
                throw new IllegalArgumentException("Page must be greater than or equal to 0");
            }

            if (size < 1 || size > 100) {
                throw new IllegalArgumentException("Page size must be between 1 and 100");
            }
        }
}