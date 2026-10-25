package com.office_management.comment_service.dto;

import com.office_management.comment_service.enums.UserRoles;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {

    private Long id;
    private Long taskId;
    private Long commentedByUserId;
    private String commenterFirstName;
    private String commenterLastName;
    private UserRoles commenterRole;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}