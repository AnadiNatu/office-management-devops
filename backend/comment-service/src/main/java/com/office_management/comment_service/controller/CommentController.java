package com.office_management.comment_service.controller;


import com.office_management.comment_service.dto.CommentRequestDTO;
import com.office_management.comment_service.dto.CommentResponseDTO;
import com.office_management.comment_service.dto.CommentUpdateDTO;
import com.office_management.comment_service.security.CustomUserDetails;
import com.office_management.comment_service.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ADD COMMENT TO TASK
    @PostMapping("/task/{taskId}")
    public ResponseEntity<CommentResponseDTO> addComment(@PathVariable Long taskId, @Valid @RequestBody CommentRequestDTO request, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        CommentResponseDTO response = commentService.addComment(taskId, request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET ALL COMMENTS FOR A TASK
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByTask(@PathVariable Long taskId, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        List<CommentResponseDTO> comments = commentService.getCommentsByTask(taskId, currentUserId);
        return ResponseEntity.ok(comments);
    }

    // GET COMMENT BY ID
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long commentId, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        CommentResponseDTO response = commentService.getCommentById(commentId, currentUserId);

        return ResponseEntity.ok(response);
    }

    // GET COMMENTS MADE BY CURRENT USER
    @GetMapping("/my")
    public ResponseEntity<List<CommentResponseDTO>> getMyComments(Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);

        List<CommentResponseDTO> comments = commentService.getCommentsByCurrentUser(currentUserId);
        return ResponseEntity.ok(comments);
    }

    // UPDATE COMMENT
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentUpdateDTO request, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);

        CommentResponseDTO response = commentService.updateComment(commentId, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    // DELETE COMMENT
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        commentService.deleteComment(commentId, currentUserId);

        return ResponseEntity.noContent().build();
    }
    // CURRENT USER ID

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Authenticated user not found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        throw new RuntimeException("Invalid authenticated user principal");
    }
}
// Security files for the comments-service files