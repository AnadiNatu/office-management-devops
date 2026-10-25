package com.office_management.comment_service.service.impl;

import com.office_management.comment_service.dto.CommentRequestDTO;
import com.office_management.comment_service.dto.CommentResponseDTO;
import com.office_management.comment_service.dto.CommentUpdateDTO;
import com.office_management.comment_service.dto.feign.TaskSummaryDTO;
import com.office_management.comment_service.dto.feign.UserSummaryDTO;
import com.office_management.comment_service.entity.TaskComments;
import com.office_management.comment_service.enums.UserRoles;
import com.office_management.comment_service.excption.errors.*;
import com.office_management.comment_service.feign.AuthUserFeignClient;
import com.office_management.comment_service.feign.TaskFeignClient;
import com.office_management.comment_service.mapper.CommentMapper;
import com.office_management.comment_service.rabbitMQ_messaging.CommentNotificationEvent;
import com.office_management.comment_service.rabbitMQ_messaging.CommentNotificationPublisher;
import com.office_management.comment_service.repository.CommentRepository;
import com.office_management.comment_service.service.CommentService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    private final AuthUserFeignClient authUserFeignClient;
    private final TaskFeignClient taskFeignClient;

    private final CommentNotificationPublisher commentNotificationPublisher;


    @Override
    public CommentResponseDTO addComment(Long taskId, CommentRequestDTO request, Long currentUserId) {
        validateTaskId(taskId);
        validateUserId(currentUserId);

        if (request == null
                || request.getComment() == null
                || request.getComment().isBlank()) {

            throw new IllegalArgumentException(
                    "Comment content is required"
            );
        }

        log.info("Adding comment to task {} by user {}", taskId, currentUserId);

        // Step 1: Validate that the authenticated user exists and is enabled.
        UserSummaryDTO currentUser = getAndValidateUser(currentUserId);

        // Step 2: Retrieve the task from Task Service. This also confirms that the task exists.
        TaskSummaryDTO task = getTask(taskId);

        //Step 3 : Check whether this user is allowed to comment on this task.
        validateCommentPermission(currentUser, task);

        // Step 4 : Convert request DTO into entity. Mapper only maps the comment content
        TaskComments comment = commentMapper.toEntity(request);

        // Step 5 : Populate server-controlled fields.
        comment.setTaskId(taskId);
        comment.setCommentedByUserId(currentUser.getId());
        comment.setCommenterFirstName(currentUser.getFname());
        comment.setCommenterLastName(currentUser.getLname());
        comment.setCommenterRole(currentUser.getRole());
        comment.setComment(request.getComment().trim());

        // Step 6 : Save comment.
        TaskComments savedComment = commentRepository.save(comment);

        List<Long> recipients = Stream.of(task.getCreatedById() , task.getAssignedToId())
                .filter(Objects::nonNull)
                .filter(id -> id > 0)
                        .filter(id -> !id.equals(currentUser.getId()))
                                .distinct()
                                        .toList();

        String preview = savedComment.getComment();
        if (preview != savedComment.getComment()){
            preview = preview.substring(0,117) + "...";
        }

        String commenterName = (currentUser.getFname() == null ? "" : currentUser.getFname()) + (currentUser.getLname()== null ? "" : " " + currentUser.getLname());
        CommentNotificationEvent event = CommentNotificationEvent.builder()
                        .eventId("comment-created-" + savedComment.getId())
                        .commentId(savedComment.getId())
                        .taskId(taskId)
                        .taskTitle(task.getTitle())
                        .commenterUserId(currentUser.getId())
                        .commenterName(commenterName.trim())
                        .commenterRole(
                                currentUser.getRole() == null
                                        ? null
                                        : currentUser.getRole().name()
                        )
                        .commentPreview(preview)
                        .recipientUserIds(recipients)
                        .occurredAt(java.time.LocalDateTime.now())
                        .build();

        if (!recipients.isEmpty()){
            commentNotificationPublisher.publishAfterCommit(event);
        }else{
            log.debug("No notification recipient for the comment {}" , savedComment.getId());
        }

        log.info("Comment {} created on task {} by user {}", savedComment.getId(), taskId, currentUserId);
        return commentMapper.toResponseDTO(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByTask(Long taskId, Long currentUserId) {
        validateTaskId(taskId);
        validateUserId(currentUserId);

        log.info("Fetching comments for task {} by user {}", taskId, currentUserId);

        //Validate current user.
        UserSummaryDTO currentUser = getAndValidateUser(currentUserId);
        // Retrieve task. This confirms that the task exists.
        TaskSummaryDTO task = getTask(taskId);
        // Make sure the user is allowed to view this task's comments.
        validateCommentViewPermission(currentUser, task);

        List<TaskComments> comments = commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);

        return comments.stream()
                .map(commentMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDTO getCommentById(Long commentId, Long currentUserId) {
        validateCommentId(commentId);
        validateUserId(currentUserId);

        UserSummaryDTO currentUser = getAndValidateUser(currentUserId);
        TaskComments comment = getComment(commentId);

        // Retrieve the task associated with this comment.
        TaskSummaryDTO task = getTask(comment.getTaskId());

        // User must be allowed to view the task's comments.
        validateCommentViewPermission(currentUser, task);

        return commentMapper.toResponseDTO(comment);
    }

    @Override
    public CommentResponseDTO updateComment(Long commentId, CommentUpdateDTO request, Long currentUserId) {
        validateCommentId(commentId);
        validateUserId(currentUserId);

        if (request == null || request.getComment() == null || request.getComment().isBlank()) {
            throw new IllegalArgumentException("Comment content is required");
        }

        log.info("Updating comment {} by user {}", commentId, currentUserId);

        UserSummaryDTO currentUser = getAndValidateUser(currentUserId);
        TaskComments comment = getComment(commentId);

        TaskSummaryDTO task = getTask(comment.getTaskId());

        // Check whether this user can edit this particular comment.
        validateCommentModificationPermission(currentUser, comment, task);
        // Only the actual comment content is changed.
        comment.setComment(request.getComment().trim());
        // @PreUpdate in TaskComment will update , updatedAt automatically.
        TaskComments updatedComment = commentRepository.save(comment);


        log.info("Comment {} updated by user {}", commentId, currentUserId);
        return commentMapper.toResponseDTO(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId, Long currentUserId) {
        validateCommentId(commentId);
        validateUserId(currentUserId);

        log.info("Deleting comment {} by user {}", commentId, currentUserId);

        UserSummaryDTO currentUser = getAndValidateUser(currentUserId);

        TaskComments comment = getComment(commentId);
        TaskSummaryDTO task = getTask(comment.getTaskId());

        validateCommentModificationPermission(currentUser, comment, task);

        commentRepository.delete(comment);
        log.info("Comment {} deleted by user {}", commentId, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByCurrentUser(Long currentUserId) {
        // Make sure user still exists and is enabled.
        validateUserId(currentUserId);

        getAndValidateUser(currentUserId);

        List<TaskComments> comments = commentRepository.findByCommentedByUserIdOrderByCreatedAtDesc(currentUserId);

        return comments.stream()
                .map(commentMapper::toResponseDTO)
                .toList();
    }

// HELPER METHOD
private UserSummaryDTO getAndValidateUser(Long userId) {
    try {
        UserSummaryDTO user = authUserFeignClient.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }

        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new UserDisabledException("User account is disabled");
        }
        return user;
    }catch(FeignException.NotFound ex){
        throw new UserNotFoundException( "User with ID " + userId + " not found" );
    }
}

    private TaskSummaryDTO getTask(Long taskId) {
        try {
            TaskSummaryDTO task = taskFeignClient.getTaskById(taskId);

            if (task == null) {
                throw new TaskNotFoundException( "Task with ID " + taskId + " not found" );
            }

            return task;
        }catch (FeignException.NotFound exception) {
            throw new TaskNotFoundException( "Task with ID " + taskId + " not found" );
        }
    }

    private TaskComments getComment(Long commentId) {

        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException( "Comment with ID " + commentId + " not found" ));
    }

    private void validateCommentPermission(UserSummaryDTO user, TaskSummaryDTO task) {
        UserRoles role = user.getRole();
        //ADMIN can comment on any task.
        if (role == UserRoles.ADMIN) {
            return;
        }

        // MANAGER can comment on tasks they created.
        if (role == UserRoles.MANAGER) {

            if (task.getCreatedById() != null && task.getCreatedById().equals(user.getId())) {
                return;
            }
            // A manager may also comment on a task assigned to them.
            if (task.getAssignedToId() != null && task.getAssignedToId().equals(user.getId())) {
                return;
            }
            throw new UnauthorizedCommentActionException( "Manager is not authorized " + "to comment on this task" );
        }

        // EMPLOYEE can comment only on a task assigned to them.
        if (role == UserRoles.EMPLOYEE) {
            if (task.getAssignedToId() != null && task.getAssignedToId().equals(user.getId())) {
                return;
            }
            throw new UnauthorizedCommentActionException( "Employee is not authorized " + "to comment on this task" );
        }
        throw new UnauthorizedCommentActionException( "User role is not authorized to comment" );
    }

    private void validateCommentViewPermission(UserSummaryDTO user, TaskSummaryDTO task) {
        UserRoles role = user.getRole();
        // ADMIN can view comments on every task.

        if (role == UserRoles.ADMIN) {
            return;
        }
        // MANAGER can view comments on tasks they created or are assigned to.
        if (role == UserRoles.MANAGER) {
            boolean createdByUser = task.getCreatedById() != null &&
                    task.getCreatedById().equals(user.getId());

            boolean assignedToUser = task.getAssignedToId() != null &&
                    task.getAssignedToId().equals(user.getId());

            if (createdByUser || assignedToUser) {
                return;
            }
            throw new UnauthorizedCommentActionException( "Manager is not authorized " + "to view comments on this task" );
        }
        // EMPLOYEE can view comments only on tasks assigned to them.
        if (role == UserRoles.EMPLOYEE) {
            if (task.getAssignedToId() != null && task.getAssignedToId().equals(user.getId())) {
                return;
            }
            throw new UnauthorizedCommentActionException( "Employee is not authorized " + "to view comments on this task" );
        }
        throw new UnauthorizedCommentActionException( "User role is not authorized " + "to view comments" );
    }

    private void validateCommentModificationPermission(UserSummaryDTO user, TaskComments comment, TaskSummaryDTO task) {

        UserRoles role = user.getRole();

        //  ADMIN can modify/delete any comment.
        if (role == UserRoles.ADMIN) {
            return;
        }
        // The person who created the comment can modify their own comment.
        boolean isCommentOwner = comment.getCommentedByUserId() != null &&
                comment.getCommentedByUserId().equals(user.getId());

        if (isCommentOwner) {
            return;
        }

        // MANAGER who created the task can also manage comments belonging to that task.
        if (role == UserRoles.MANAGER) {
            boolean createdTask = task.getCreatedById() != null &&
            task.getCreatedById().equals(user.getId());

            if (createdTask) {return;}
        }
        throw new UnauthorizedCommentActionException( "User is not authorized " + "to modify this comment" );
    }

    private void validateCommentId(Long commentId) {
        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException("Comment ID must be positive");
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }

    private void validateTaskId(Long taskId) {
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }
    }
}