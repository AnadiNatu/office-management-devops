package com.office_management.comment_service.dto.feign;


import com.office_management.comment_service.enums.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private Long createdById;
    private Long assignedToId;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
}