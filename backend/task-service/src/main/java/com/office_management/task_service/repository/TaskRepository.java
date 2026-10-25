package com.office_management.task_service.repository;

import com.office_management.task_service.entity.Task;
import com.office_management.task_service.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task , Long> {
    List<Task> findByCreatedById(Long createdById);
    List<Task> findByAssignedToId(Long assignedToId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByAssignedToIdAndStatus(Long assignedToId, TaskStatus status);
    List<Task> findByCreatedByIdAndStatus(Long createdById, TaskStatus status);
    boolean existsByIdAndAssignedToId(Long taskId, Long assignedToId);
}