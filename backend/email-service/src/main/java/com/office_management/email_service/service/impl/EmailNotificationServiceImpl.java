package com.office_management.email_service.service.impl;

import com.office_management.email_service.dto.EmailRequestDTO;
import com.office_management.email_service.dto.TaskEventMessage;
import com.office_management.email_service.dto.UserEmailDTO;
import com.office_management.email_service.enums.TaskEventType;
import com.office_management.email_service.service.EmailNotificationService;
import com.office_management.email_service.service.EmailTemplateService;
import com.office_management.email_service.service.MailService;
import com.office_management.email_service.service.RecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationServiceImpl implements EmailNotificationService {
    private final RecipientService recipientService;
    private final EmailTemplateService emailTemplateService;
    private final MailService mailService;

    @Override public void processTaskEvent(TaskEventMessage event) {
        if (event == null) {
            log.warn("Received null task event");
            return;
        }

        if (event.getEventType() == null) {
            log.warn( "Received task event {} without event type", event.getEventId());
            return;
        }

        log.info( "Processing email notification for event {} of type {}", event.getEventId(), event.getEventType());

        Set<Long> recipientIds = resolveRecipientIds(event);

        if (recipientIds.isEmpty()) {
            log.info( "No recipients found for task event {}", event.getEventId() );
            return;
        }

        for (Long recipientId : recipientIds) {
            try {
                UserEmailDTO recipient = recipientService.getUserById(recipientId);
                if (recipient == null) { continue; }

                if (Boolean.FALSE.equals(recipient.getEnabled())) { log.info( "Skipping email for disabled user {}", recipientId );
                    continue;
                }

                if (recipient.getEmail() == null || recipient.getEmail().isBlank()) {
                    log.info( "Skipping email for user {} because no email address is available", recipientId );
                    continue;
                }
                EmailRequestDTO email = buildEmail(event, recipient);
                if (email == null) {
                    log.warn( "No email template available for event type {}", event.getEventType());
                    continue;
                }

                mailService.sendEmail(email);
            } catch (Exception exception) {
                log.error( "Failed to process email notification for user {} and event {}", recipientId, event.getEventId(), exception );
            }
        }
    }

    private Set<Long> resolveRecipientIds(TaskEventMessage event ) {
        Set<Long> recipientIds = new HashSet<>();
        TaskEventType eventType = event.getEventType();
        switch (eventType)
        {
            case TASK_CREATED -> { addIfPresent( recipientIds, event.getCreatedById() ); addIfPresent( recipientIds, event.getAssignedToId() ); }
            case TASK_ASSIGNED -> addIfPresent( recipientIds, event.getAssignedToId());
            case TASK_REASSIGNED -> { addIfPresent( recipientIds, event.getPreviousAssignedToId() ); addIfPresent( recipientIds, event.getAssignedToId() ); }
            case TASK_STATUS_UPDATED -> { addIfPresent( recipientIds, event.getCreatedById() ); addIfPresent( recipientIds, event.getAssignedToId() ); }
            case TASK_COMPLETED -> { addIfPresent( recipientIds, event.getCreatedById() ); addIfPresent( recipientIds, event.getAssignedToId() ); }
            case TASK_DELETED -> { addIfPresent( recipientIds, event.getCreatedById() ); addIfPresent( recipientIds, event.getAssignedToId() ); }
        }
        return recipientIds;
    }

    // Alternate resolution logic if the task completed by some user who has completed the task assigned to himself , like a manager or an admin.
//    private Set<Long> resolveRecipientIds( TaskEventMessage event ) { Set<Long> recipientIds = new HashSet<>(); TaskEventType eventType = event.getEventType(); switch (eventType) { case TASK_CREATED -> { addIfPresent( recipientIds, event.getCreatedById() ); addIfPresent( recipientIds, event.getAssignedToId() ); } case TASK_ASSIGNED -> { addIfPresent( recipientIds, event.getAssignedToId() ); } case TASK_REASSIGNED -> { addIfPresent( recipientIds, event.getPreviousAssignedToId() ); addIfPresent( recipientIds, event.getAssignedToId() ); } case TASK_STATUS_UPDATED, TASK_COMPLETED -> { addIfPresentExceptActor( recipientIds, event.getCreatedById(), event.getChangedById() ); addIfPresentExceptActor( recipientIds, event.getAssignedToId(), event.getChangedById() ); } case TASK_DELETED -> { addIfPresentExceptActor( recipientIds, event.getCreatedById(), event.getChangedById() ); addIfPresentExceptActor( recipientIds, event.getAssignedToId(), event.getChangedById() ); } } return recipientIds; } private void addIfPresent( Set<Long> recipientIds, Long userId ) { if (userId != null) { recipientIds.add(userId); } } private void addIfPresentExceptActor( Set<Long> recipientIds, Long userId, Long actorId ) { if (userId != null && !userId.equals(actorId)) { recipientIds.add(userId); } }

    private EmailRequestDTO buildEmail(TaskEventMessage event, UserEmailDTO recipient ) {
        return
                switch (event.getEventType()) {
            case TASK_CREATED -> emailTemplateService.buildTaskCreatedEmail( event, recipient );
            case TASK_ASSIGNED -> emailTemplateService.buildTaskAssignedEmail( event, recipient );
            case TASK_REASSIGNED -> emailTemplateService.buildTaskReassignedEmail( event, recipient );
            case TASK_STATUS_UPDATED -> emailTemplateService.buildTaskStatusUpdatedEmail( event, recipient );
            case TASK_COMPLETED -> emailTemplateService.buildTaskCompletedEmail( event, recipient );
            case TASK_DELETED -> emailTemplateService.buildTaskDeletedEmail( event, recipient );
        };
    }

    private void addIfPresent(Set<Long> recipientIds, Long userId ) {
        if (userId != null) {
            recipientIds.add(userId);
        }
    }
}