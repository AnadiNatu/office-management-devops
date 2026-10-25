package com.office_management.task_service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequestDTO {

    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Task title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Task description is required")
    @Size(max = 5000, message = "Task description cannot exceed 5000 characters")
    private String description;

    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDateTime dueDate;
}