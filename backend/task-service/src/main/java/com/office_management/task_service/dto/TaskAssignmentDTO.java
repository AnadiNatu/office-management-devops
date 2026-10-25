package com.office_management.task_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskAssignmentDTO {
    @NotNull(message = "Assigned user ID is required")
    private Long assignedToId;
}