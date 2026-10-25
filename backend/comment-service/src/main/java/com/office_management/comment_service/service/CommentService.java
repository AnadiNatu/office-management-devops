package com.office_management.comment_service.service;

import com.office_management.comment_service.dto.CommentRequestDTO;
import com.office_management.comment_service.dto.CommentResponseDTO;
import com.office_management.comment_service.dto.CommentUpdateDTO;

import java.util.List;

public interface CommentService {
    CommentResponseDTO addComment(Long taskId, CommentRequestDTO request, Long currentUserId);
    List<CommentResponseDTO> getCommentsByTask(Long taskId, Long currentUserId);

    CommentResponseDTO getCommentById(Long commentId, Long currentUserId);
    CommentResponseDTO updateComment(Long commentId, CommentUpdateDTO request, Long currentUserId);

    void deleteComment(Long commentId, Long currentUserId);
    List<CommentResponseDTO> getCommentsByCurrentUser(Long currentUserId);
}