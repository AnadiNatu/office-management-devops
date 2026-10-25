package com.office_management.comment_service.mapper;

import com.office_management.comment_service.dto.CommentRequestDTO;
import com.office_management.comment_service.dto.CommentResponseDTO;
import com.office_management.comment_service.entity.TaskComments;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public TaskComments toEntity(CommentRequestDTO request) {
        if (request == null) {
            return null;
        }
        return TaskComments.builder().comment(request.getComment().trim()).build();
    }

    public CommentResponseDTO toResponseDTO(TaskComments comment) {

        if (comment == null) {
            return null;
        }

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .taskId(comment.getTaskId())
                .commentedByUserId(comment.getCommentedByUserId())
                .commenterFirstName(comment.getCommenterFirstName())
                .commenterLastName(comment.getCommenterLastName())
                .commenterRole(comment.getCommenterRole())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}