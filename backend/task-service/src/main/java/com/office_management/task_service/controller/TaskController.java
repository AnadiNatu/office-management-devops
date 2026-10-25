package com.office_management.task_service.controller;

import com.office_management.task_service.dto.TaskAssignmentDTO;
import com.office_management.task_service.dto.TaskRequestDTO;
import com.office_management.task_service.dto.TaskResponseDTO;
import com.office_management.task_service.dto.TaskStatusUpdateDTO;
import com.office_management.task_service.security.CustomUserDetails;
import com.office_management.task_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO request, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        TaskResponseDTO response = taskService.createTask(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long taskId, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        TaskResponseDTO response = taskService.getTaskById(taskId, currentUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskResponseDTO>> getMyTasks(Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        List<TaskResponseDTO> response = taskService.getTasksForCurrentUser(currentUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allTask")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(Authentication authentication) {

        Long currentUserId = getCurrentUserId(authentication);
        List<TaskResponseDTO> response = taskService.getAllTasks(currentUserId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/assign")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<TaskResponseDTO> assignTask(@PathVariable Long taskId, @Valid @RequestBody TaskAssignmentDTO request, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        TaskResponseDTO response = taskService.assignTask(taskId, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/reassign")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<TaskResponseDTO> reassignTask(@PathVariable Long taskId, @Valid @RequestBody TaskAssignmentDTO request, Authentication authentication) {

        Long currentUserId = getCurrentUserId(authentication);
        TaskResponseDTO response = taskService.reassignTask(taskId, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(@PathVariable Long taskId, @Valid @RequestBody TaskStatusUpdateDTO request, Authentication authentication) {

        Long currentUserId = getCurrentUserId(authentication);
        TaskResponseDTO response = taskService.updateTaskStatus(taskId, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskRequestDTO request, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        TaskResponseDTO response = taskService.updateTask(taskId, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        taskService.deleteTask(taskId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authenticated user not found");
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        throw new IllegalStateException("Invalid authenticated user principal");
    }
}

// Security files for the task-service
// Transferring of task and user related data to the comment-service
// And the email-service notifications on task completion , assignment , reassignment
// Hey chat I want you to add reassign functionality or equivalent functionality by which the task can be reassigned to other employees  with email notifications and same flow  we have for the other functionalities .
// And chat I want to ask whether or not the emails and the notifications functionalities  will work with the changes done to the task entities via the task controller endpoints , because I dont see any feign setup done betwenn the task-service , email-service & notification-service .


