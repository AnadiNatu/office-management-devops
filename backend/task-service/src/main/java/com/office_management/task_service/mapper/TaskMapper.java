package com.office_management.task_service.mapper;

import com.office_management.task_service.dto.TaskRequestDTO;
import com.office_management.task_service.dto.TaskResponseDTO;
import com.office_management.task_service.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequestDTO dto) {

        if (dto == null) {return null;}

        return Task.builder()
                .title(dto.getTitle().trim())
                .description(dto.getDescription().trim())
                .dueDate(dto.getDueDate())
                .build();
    }

    public TaskResponseDTO toResponseDTO(Task task) {

        if (task == null) {return null;}

        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .createdById(task.getCreatedById())
                .assignedToId(task.getAssignedToId())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .dueDate(task.getDueDate())
                .build();
    }

}