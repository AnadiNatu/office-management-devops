package com.office_management.email_service.service;

import com.office_management.email_service.dto.EmailRequestDTO;
import com.office_management.email_service.dto.TaskEventMessage;
import com.office_management.email_service.dto.UserEmailDTO;

public interface EmailTemplateService {

    EmailRequestDTO buildTaskCreatedEmail(TaskEventMessage event, UserEmailDTO recipient ); EmailRequestDTO buildTaskAssignedEmail(TaskEventMessage event, UserEmailDTO recipient ); EmailRequestDTO buildTaskReassignedEmail(TaskEventMessage event, UserEmailDTO recipient );
    EmailRequestDTO buildTaskStatusUpdatedEmail(TaskEventMessage event, UserEmailDTO recipient );
    EmailRequestDTO buildTaskCompletedEmail( TaskEventMessage event, UserEmailDTO recipient );
    EmailRequestDTO buildTaskDeletedEmail( TaskEventMessage event, UserEmailDTO recipient );

}