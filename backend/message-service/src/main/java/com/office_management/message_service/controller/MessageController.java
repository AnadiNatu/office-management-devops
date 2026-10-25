package com.office_management.message_service.controller;

import com.office_management.message_service.dto.MessageCreateRequestDTO;
import com.office_management.message_service.dto.MessageResponseDTO;
import com.office_management.message_service.dto.PageResponseDTO;
import com.office_management.message_service.security.SecurityUtil;
import com.office_management.message_service.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendMessage(@PathVariable Long conversationId,
            @Valid @RequestBody MessageCreateRequestDTO request) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        MessageResponseDTO response = messageService.sendMessage(currentUserId, conversationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<MessageResponseDTO>> getMessages(@PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size)
    {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(messageService.getMessages(currentUserId, conversationId, page, size));
    }
}