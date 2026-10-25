package com.office_management.message_service.service.impl;

import com.office_management.message_service.dto.MessageCreateRequestDTO;
import com.office_management.message_service.dto.MessageResponseDTO;
import com.office_management.message_service.dto.PageResponseDTO;
import com.office_management.message_service.entity.ConversationParticipant;
import com.office_management.message_service.entity.Message;
import com.office_management.message_service.enums.MessageStatus;
import com.office_management.message_service.exception.errors.InvalidConversationException;
import com.office_management.message_service.exception.errors.ResourceNotFoundException;
import com.office_management.message_service.mapper.MessageMapper;
import com.office_management.message_service.rabbitMQ_messaging.MessageNotificationEvent;
import com.office_management.message_service.rabbitMQ_messaging.MessageNotificationPublisher;
import com.office_management.message_service.repository.ConversationParticipantRepository;
import com.office_management.message_service.repository.MessageRepository;
import com.office_management.message_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageMapper messageMapper;
    private final MessageNotificationPublisher messageNotificationPublisher;

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(Long currentUserId,Long conversationId, MessageCreateRequestDTO request) {

        validateUserId(currentUserId);
        validateConversationId(conversationId);

        // Verify that the sender belongs to the conversation.
        verifyParticipant(conversationId, currentUserId);
         //Find the other participant.
        List<ConversationParticipant> participants = participantRepository.findByConversationId(conversationId);

        if (participants.size() != 2) {
            throw new InvalidConversationException(
                    "One-to-one conversation must contain exactly two participants"
            );
        }

        Long recipientUserId = participants.stream()
                        .map(ConversationParticipant::getUserId)
                        .filter(userId -> !userId.equals(currentUserId))
                        .findFirst()
                        .orElseThrow(() -> new InvalidConversationException("Conversation does not have a valid recipient"));

        if (participants.size() != 2) {
            throw new InvalidConversationException("One-to-one conversation must contain exactly two participants");
        }

        //Create message.
        Message message = Message.builder()
                        .conversationId(conversationId)
                        .senderUserId(currentUserId)
                        .recipientUserId(recipientUserId)
                        .content(request.getContent().trim())
                        .status(MessageStatus.SENT)
                        .build();

        message = messageRepository.save(message);

        String preview = message.getContent();
        if (preview != null && preview.length() > 120){
            preview = preview.substring(0 , 117) + "...";
        }

        MessageNotificationEvent event = MessageNotificationEvent.builder()
                .eventId("message-received-"+message.getId()+"-"+recipientUserId)
                .messageId(message.getId())
                .conversationId(conversationId)
                .senderUserId(currentUserId)
                .recipientUserId(recipientUserId)
                .contentPreview(preview)
                .occurredAt(LocalDateTime.now())
                .build();

        messageNotificationPublisher.publishAfterCommit(event);

        return messageMapper.toResponseDTO(message);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<MessageResponseDTO>
    getMessages(Long currentUserId, Long conversationId, int page, int size) {

        validateUserId(currentUserId);
        validateConversationId(conversationId);
        validatePagination(page, size);

        // Authorization:- only conversation participants can read messages.

        verifyParticipant(conversationId, currentUserId);

        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId, pageable);

        return messageMapper.toPageResponseDTO(messages);
    }

    @Override
    @Transactional
    public MessageResponseDTO markAsDelivered(Long currentUserId, Long messageId) {

        Message message = getMessageForRecipient(currentUserId,messageId);

        if (message.getStatus() == MessageStatus.SENT) {

            message.setStatus(MessageStatus.DELIVERED);
            message.setDeliveredAt(LocalDateTime.now());

            message = messageRepository.save(message);
        }

        return messageMapper.toResponseDTO(message);
    }

    @Override
    @Transactional
    public MessageResponseDTO markAsRead(Long currentUserId, Long messageId) {

        Message message = getMessageForRecipient(currentUserId, messageId);
        if (message.getStatus() != MessageStatus.READ) {

            message.setStatus(MessageStatus.READ);

            if (message.getDeliveredAt() == null) {
                message.setDeliveredAt(LocalDateTime.now());
            }

            message.setReadAt(LocalDateTime.now());
            message = messageRepository.save(message);
        }

        return messageMapper.toResponseDTO(message);
    }

    private Message getMessageForRecipient(Long currentUserId, Long messageId) {

        validateUserId(currentUserId);

        if (messageId == null || messageId <= 0) {
            throw new IllegalArgumentException("Message ID must be positive");
        }

        Message message = messageRepository.findById(messageId).orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        // Only the recipient can update delivery/read status.
        if (!message.getRecipientUserId().equals(currentUserId)) {
            throw new InvalidConversationException("You are not allowed to update this message");
        }

        return message;
    }

    private void verifyParticipant(Long conversationId, Long userId) {
        boolean participant = participantRepository.existsByConversationIdAndUserId(conversationId, userId);

        if (!participant) {
            throw new InvalidConversationException("You are not a participant of this conversation");
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }

    private void validateConversationId(Long conversationId) {
        if (conversationId == null || conversationId <= 0) {
            throw new IllegalArgumentException("Conversation ID must be positive");
        }
    }

    private void validatePagination(int page, int size){

        if (page < 0) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
    }
}
