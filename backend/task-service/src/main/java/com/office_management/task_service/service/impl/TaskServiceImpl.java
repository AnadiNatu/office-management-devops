package com.office_management.task_service.service.impl;

import com.office_management.task_service.dto.TaskAssignmentDTO;
import com.office_management.task_service.dto.TaskRequestDTO;
import com.office_management.task_service.dto.TaskResponseDTO;
import com.office_management.task_service.dto.TaskStatusUpdateDTO;
import com.office_management.task_service.dto.feign.UserSummaryDTO;
import com.office_management.task_service.entity.Task;
import com.office_management.task_service.enums.TaskStatus;
import com.office_management.task_service.enums.UserRoles;
import com.office_management.task_service.exception.errors.*;
import com.office_management.task_service.feign.AuthUserFeignClient;
import com.office_management.task_service.mapper.TaskMapper;
import com.office_management.task_service.rabbit_messaging.TaskEventPublisher;
import com.office_management.task_service.repository.TaskRepository;
import com.office_management.task_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl  implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final AuthUserFeignClient authUserFeignClient;
    private final TaskEventPublisher taskEventPublisher;

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO request, Long currentUserId) {

        UserSummaryDTO currentUser = getRequiredUser(currentUserId);
        validateUserEnabled(currentUser);

        if (!canCreateTask(currentUser.getRole())) {
            throw new UnauthorizedTaskActionException(
                    "You are not authorized to create tasks"
            );
        }


        Task task = taskMapper.toEntity(request);

        task.setCreatedById(currentUserId);
        task.setStatus(TaskStatus.CREATED);

        Task savedTask = taskRepository.save(task);

//        Rabbit MQ task created event
        taskEventPublisher.publishTaskCreated(savedTask , currentUserId);

        log.info("Task created. taskId={}, createdById={}", savedTask.getId(), currentUserId);
        return taskMapper.toResponseDTO(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long taskId, Long currentUserId) {

        Task task = getTask(taskId);
        UserSummaryDTO currentUser = getRequiredUser(currentUserId);
        validateUserEnabled(currentUser);

        if (!canViewTask(task, currentUser)) {
            throw new UnauthorizedTaskActionException("You are not authorized to view this task");
        }

        return taskMapper.toResponseDTO(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksForCurrentUser(Long currentUserId) {

        UserSummaryDTO currentUser = getRequiredUser(currentUserId);
        validateUserEnabled(currentUser);
        List<Task> tasks;

//        if (currentUser.getRole() == UserRoles.EMPLOYEE) {
//            tasks = taskRepository.findByAssignedToId(currentUserId);
//        } else {
//            tasks = taskRepository.findByCreatedById(currentUserId);
//        }

        if (currentUser.getRole() == UserRoles.EMPLOYEE) {
            tasks = taskRepository.findByAssignedToId(currentUserId);
        } else if (currentUser.getRole() == UserRoles.MANAGER) {
            tasks = taskRepository.findByCreatedById(currentUserId);
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks.stream().map(taskMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks(Long currentUserId) {

        UserSummaryDTO currentUser = getRequiredUser(currentUserId);

        validateUserEnabled(currentUser);

        if (!canViewAllTasks(currentUser.getRole())) {
            throw new UnauthorizedTaskActionException("You are not authorized to view all tasks");
        }

        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toResponseDTO)
                .toList();
    }

    @Override
    public TaskResponseDTO assignTask(Long taskId, TaskAssignmentDTO request, Long currentUserId) {

        Task task = getTask(taskId);
        UserSummaryDTO currentUser = getRequiredUser(currentUserId);
        validateUserEnabled(currentUser);

        if (!canAssignTask(currentUser.getRole())) {
            throw new UnauthorizedTaskActionException("You are not authorized to assign tasks");
        }

        if (task.getAssignedToId() != null) {
            throw new InvalidTaskStatusException("Task is already assigned. Use the reassign endpoint instead.");
        }

        UserSummaryDTO assignedUser = getRequiredUser(request.getAssignedToId());

        validateUserEnabled(assignedUser);

        if (assignedUser.getRole() != UserRoles.EMPLOYEE) {
            throw new UnauthorizedTaskActionException("Tasks can only be assigned to employees");
        }

        task.setAssignedToId(assignedUser.getId());

        if (task.getStatus() == TaskStatus.CREATED) {
            task.setStatus(TaskStatus.ASSIGNED);
        }
        Task savedTask = taskRepository.save(task);

        taskEventPublisher.publishTaskAssigned(savedTask , currentUserId);

        log.info("Task assigned. taskId={}, assignedToId={}, assignedById={}", taskId, assignedUser.getId(), currentUserId);
        return taskMapper.toResponseDTO(savedTask);
    }

    @Override
    public TaskResponseDTO reassignTask(Long taskId, TaskAssignmentDTO request, Long currentUserId) {

        Task task = getTask(taskId);
        UserSummaryDTO currentUser = getRequiredUser(currentUserId);

        validateUserEnabled(currentUser);

        if (!canAssignTask(currentUser.getRole())) {
            throw new UnauthorizedTaskActionException("You are not authorized to reassign tasks");
        }

        if (request == null || request.getAssignedToId() == null) {
            throw new IllegalArgumentException("New assignee is required");
        }

        Long previousAssignedToId = task.getAssignedToId();

        if (previousAssignedToId == null) {
            throw new InvalidTaskStatusException("Task is not currently assigned");
        }

        if (previousAssignedToId.equals(request.getAssignedToId())) {
            throw new IllegalArgumentException("Task is already assigned to this employee");
        }

        UserSummaryDTO newAssignee = getRequiredUser(request.getAssignedToId());

        validateUserEnabled(newAssignee);
        if (newAssignee.getRole() != UserRoles.EMPLOYEE) {
            throw new UnauthorizedTaskActionException("Tasks can only be assigned to employees");
        }

        task.setAssignedToId(newAssignee.getId());

        Task savedTask = taskRepository.save(task);
        taskEventPublisher.publishTaskReassigned(savedTask, previousAssignedToId, currentUserId);

        log.info("Task reassigned. taskId={}, previousAssignedToId={}, newAssignedToId={}, changedById={}", taskId, previousAssignedToId, newAssignee.getId(), currentUserId);
        return taskMapper.toResponseDTO(savedTask);
    }

    @Override
    public TaskResponseDTO updateTaskStatus(Long taskId, TaskStatusUpdateDTO request, Long currentUserId) {

        Task task = getTask(taskId);
        UserSummaryDTO currentUser = getRequiredUser(currentUserId);

        validateUserEnabled(currentUser);
        if (!canUpdateTaskStatus(task, currentUser)) {
            throw new UnauthorizedTaskActionException("You are not authorized to update this task's status");
        }

        TaskStatus oldStatus = task.getStatus();

        validateStatusTransition(oldStatus, request.getStatus());
        task.setStatus(request.getStatus());
        Task savedTask = taskRepository.save(task);

        if (savedTask.getStatus() == TaskStatus.COMPLETED) {
            taskEventPublisher.publishTaskCompleted(savedTask, oldStatus, currentUserId);
        } else {
            taskEventPublisher.publishTaskStatusUpdated(savedTask, oldStatus, currentUserId);
        }

        log.info("Task status updated. taskId={}, oldStatus={}, newStatus={}, userId={}", taskId, oldStatus, request.getStatus(), currentUserId);
        return taskMapper.toResponseDTO(savedTask);
    }

    @Override
    public TaskResponseDTO updateTask(Long taskId, TaskRequestDTO request, Long currentUserId) {

        Task task = getTask(taskId);
        UserSummaryDTO currentUser = getRequiredUser(currentUserId);

        validateUserEnabled(currentUser);

        if (!canModifyTask(task, currentUser)) {
            throw new UnauthorizedTaskActionException("You are not authorized to modify this task");
        }

        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription().trim());
        task.setDueDate(request.getDueDate());

        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponseDTO(savedTask);
    }

    // Need to make the Reassignment method

    @Override
    public void deleteTask(Long taskId, Long currentUserId) {

        Task task = getTask(taskId);

        UserSummaryDTO currentUser = getRequiredUser(currentUserId);

        validateUserEnabled(currentUser);
        if (!canDeleteTask(task, currentUser)) {
            throw new UnauthorizedTaskActionException("You are not authorized to delete this task");
        }

        taskEventPublisher.publishTaskDeleted(task, currentUserId);

        taskRepository.delete(task);
        log.info("Task deleted. taskId={}, deletedById={}", taskId, currentUserId);
    }

    private Task getTask(Long taskId) {

        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
    }

    private UserSummaryDTO getRequiredUser(Long userId) {

        try {
            UserSummaryDTO user = authUserFeignClient.getUserById(userId);

            if (user == null) {
                throw new UserNotFoundException("User not found with id: " + userId);
            }

            return user;

        } catch (UserNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to retrieve user from Auth Service. userId={}", userId, ex);
            throw new UserNotFoundException("Unable to validate user with id: " + userId);
        }
    }

    private void validateUserEnabled(UserSummaryDTO user) {

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new UserDisabledException("User account is disabled");
        }
    }

    private boolean canCreateTask(UserRoles role) {
        return role == UserRoles.ADMIN || role == UserRoles.MANAGER;
    }

    private boolean canAssignTask(UserRoles role) {
        return role == UserRoles.ADMIN || role == UserRoles.MANAGER;
    }

    private boolean canViewAllTasks(UserRoles role) {
        return role == UserRoles.ADMIN || role == UserRoles.MANAGER;
    }

    private boolean canViewTask(Task task, UserSummaryDTO user) {

        if (user.getRole() == UserRoles.ADMIN) {
            return true;
        }

        if (user.getRole() == UserRoles.MANAGER) {
            return task.getCreatedById().equals(user.getId());
        }

        return task.getAssignedToId() != null && task.getAssignedToId().equals(user.getId());
    }

    private boolean canModifyTask(Task task, UserSummaryDTO user) {

        if (user.getRole() == UserRoles.ADMIN) {
            return true;
        }

        return user.getRole() == UserRoles.MANAGER
                && task.getCreatedById()
                .equals(user.getId());
    }

    private boolean canDeleteTask(Task task, UserSummaryDTO user) {

        if (user.getRole() == UserRoles.ADMIN) {
            return true;
        }

        return user.getRole() == UserRoles.MANAGER
                && task.getCreatedById()
                .equals(user.getId());
    }

    private boolean canUpdateTaskStatus(Task task, UserSummaryDTO user) {

        if (user.getRole() == UserRoles.ADMIN) {
            return true;
        }

        if (user.getRole() == UserRoles.MANAGER) {
            return task.getCreatedById().equals(user.getId());
        }

        return task.getAssignedToId() != null
                && task.getAssignedToId().equals(user.getId());
    }

    private void validateStatusTransition(TaskStatus currentStatus, TaskStatus newStatus) {

        if (currentStatus == newStatus) {
            throw new InvalidTaskStatusException("Task is already in status: " + newStatus);
        }

        boolean validTransition =
                switch (currentStatus) {
                    case CREATED -> newStatus == TaskStatus.ASSIGNED || newStatus == TaskStatus.CANCELLED;
                    case ASSIGNED -> newStatus == TaskStatus.IN_PROGRESS || newStatus == TaskStatus.CANCELLED;
                    case IN_PROGRESS -> newStatus == TaskStatus.COMPLETED || newStatus == TaskStatus.CANCELLED;
                    case COMPLETED, CANCELLED -> false;
                };

        if (!validTransition) {
            throw new InvalidTaskStatusException("Invalid task status transition from " + currentStatus + " to " + newStatus);
        }
    }
}