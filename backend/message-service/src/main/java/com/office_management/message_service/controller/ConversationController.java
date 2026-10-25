package com.office_management.message_service.controller;


import com.office_management.message_service.dto.ConversationCreateRequestDTO;
import com.office_management.message_service.dto.ConversationResponseDTO;
import com.office_management.message_service.dto.ConversationSummaryDTO;
import com.office_management.message_service.dto.PageResponseDTO;
import com.office_management.message_service.security.SecurityUtil;
import com.office_management.message_service.service.ConversationService;
import com.thoughtworks.xstream.core.SecurityUtils;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping("/create-convo")
    public ResponseEntity<ConversationResponseDTO> createConversation(@Valid @RequestBody ConversationCreateRequestDTO request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        ConversationResponseDTO response = conversationService.createConversation(currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationResponseDTO>
    getConversation(@PathVariable Long conversationId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(conversationService.getConversation(currentUserId, conversationId));
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponseDTO<ConversationSummaryDTO>> getMyConversations(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(conversationService.getMyConversations(currentUserId, page, size));
    }
}