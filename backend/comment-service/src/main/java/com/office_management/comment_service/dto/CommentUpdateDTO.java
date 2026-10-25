package com.office_management.comment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateDTO {
    @NotBlank(message = "Comment content is required")
    @Size(max = 5000, message = "Comment cannot exceed 5000 characters")
    private String comment;
}