package com.office_management.comment_service.repository;

import com.office_management.comment_service.entity.TaskComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository  extends JpaRepository<TaskComments, Long> {
    List<TaskComments> findByTaskIdOrderByCreatedAtAsc(Long taskId);
    List<TaskComments> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    List<TaskComments> findByCommentedByUserIdOrderByCreatedAtDesc(Long commentedByUserId);
    long countByTaskId(Long taskId);
    long countByCommentedByUserId(Long commentedByUserId);
}