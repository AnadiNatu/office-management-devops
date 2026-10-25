package com.office_management.message_service.repository;

import com.office_management.message_service.entity.Conversation;
import com.office_management.message_service.enums.ConversationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ConversationRepository  extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByIdAndType(Long id, ConversationType type);
    Page<Conversation> findByIdInOrderByUpdatedAtDesc(Collection<Long> ids, Pageable pageable);
}
