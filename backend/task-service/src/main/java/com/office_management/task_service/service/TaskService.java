package com.office_management.task_service.service;

import com.office_management.task_service.dto.TaskAssignmentDTO;
import com.office_management.task_service.dto.TaskRequestDTO;
import com.office_management.task_service.dto.TaskResponseDTO;
import com.office_management.task_service.dto.TaskStatusUpdateDTO;
import com.office_management.task_service.entity.Task;

import java.util.List;

public interface TaskService {
    TaskResponseDTO createTask(TaskRequestDTO request, Long currentUserId);
    TaskResponseDTO getTaskById(Long taskId, Long currentUserId);
    List<TaskResponseDTO> getTasksForCurrentUser(Long currentUserId);
    List<TaskResponseDTO> getAllTasks(Long currentUserId);
    TaskResponseDTO assignTask(Long taskId, TaskAssignmentDTO request, Long currentUserId);
    TaskResponseDTO updateTaskStatus(Long taskId, TaskStatusUpdateDTO request, Long currentUserId);
    TaskResponseDTO updateTask(Long taskId, TaskRequestDTO request, Long currentUserId);
    void deleteTask(Long taskId, Long currentUserId);
    TaskResponseDTO reassignTask(Long taskId , TaskAssignmentDTO request , Long currentUserId);

}
// Need to add a reassign method that will help in reassigning the task that was
// to a EMPLOYEE can be transferred to another if the user can't complete the task.
