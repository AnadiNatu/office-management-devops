package com.office_management.message_service.repository;

import com.office_management.message_service.entity.Message;
import com.office_management.message_service.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

        Page<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId, Pageable pageable);

        Page<Message> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

        Page<Message> findByRecipientUserIdAndStatus(Long recipientUserId, MessageStatus status, Pageable pageable);

        long countByRecipientUserIdAndStatus(Long recipientUserId, MessageStatus status);
}