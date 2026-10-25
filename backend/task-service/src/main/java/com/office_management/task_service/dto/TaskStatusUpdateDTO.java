package com.office_management.task_service.dto;

import com.office_management.task_service.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusUpdateDTO {

    @NotNull(message = "Task status is required")
    private TaskStatus status;

}