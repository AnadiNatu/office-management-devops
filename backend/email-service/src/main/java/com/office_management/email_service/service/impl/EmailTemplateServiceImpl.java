package com.office_management.email_service.service.impl;

import com.office_management.email_service.dto.EmailRequestDTO;
import com.office_management.email_service.dto.TaskEventMessage;
import com.office_management.email_service.dto.UserEmailDTO;
import com.office_management.email_service.service.EmailTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailTemplateServiceImpl implements EmailTemplateService {
    private static final String APP_NAME = "Office Management System";

    @Override
    public EmailRequestDTO buildTaskCreatedEmail(TaskEventMessage event, UserEmailDTO recipient ) {
        String recipientName = getRecipientName(recipient);
        String subject = "New Task Created: " + event.getTitle();

        String body = buildHtml( "New Task Created", "Hello " + recipientName + ",",
                "A new task has been created in the Office Management System.",
                event, null, null );

        return buildEmailRequest( recipient.getEmail(), subject, body );
    }

    @Override public EmailRequestDTO buildTaskAssignedEmail(TaskEventMessage event, UserEmailDTO recipient ) {
        String recipientName = getRecipientName(recipient);
        String subject = "Task Assigned to You: " + event.getTitle();

        String body = buildHtml( "Task Assigned", "Hello " + recipientName + ",",
                "A task has been assigned to you.",
                event, null, null );

        return buildEmailRequest( recipient.getEmail(), subject, body );
    }

    @Override public EmailRequestDTO buildTaskReassignedEmail(TaskEventMessage event, UserEmailDTO recipient ) {
        String recipientName = getRecipientName(recipient);
        String subject = "Task Reassigned: " + event.getTitle();

        String body = buildHtml( "Task Reassigned", "Hello " + recipientName + ",",
                "The assignment of a task has been changed.",
                event, event.getPreviousAssignedToId(), event.getAssignedToId() );

        return buildEmailRequest( recipient.getEmail(), subject, body );

    }

    @Override public EmailRequestDTO buildTaskStatusUpdatedEmail(TaskEventMessage event, UserEmailDTO recipient ) {

        String recipientName = getRecipientName(recipient);
        String subject = "Task Status Updated: " + event.getTitle();

        String body = buildHtml( "Task Status Updated", "Hello " + recipientName + ",",
                "The status of a task has been updated.",
                event, null, null );

        return buildEmailRequest( recipient.getEmail(), subject, body );

    }

    @Override public EmailRequestDTO buildTaskCompletedEmail(TaskEventMessage event, UserEmailDTO recipient ) {
        String recipientName = getRecipientName(recipient);
        String subject = "Task Completed: " + event.getTitle();

        String body = buildHtml( "Task Completed", "Hello " + recipientName + ",",
                "A task has been marked as completed.",
                event, null, null );

        return buildEmailRequest( recipient.getEmail(), subject, body );

    }

    @Override public EmailRequestDTO buildTaskDeletedEmail(TaskEventMessage event, UserEmailDTO recipient ) {
        String recipientName = getRecipientName(recipient);
        String subject = "Task Deleted: " + event.getTitle();

        String body = buildHtml( "Task Deleted", "Hello " + recipientName + ",",
                "A task has been deleted from the Office Management System.",
                event, null, null );

        return buildEmailRequest( recipient.getEmail(), subject, body );
    }

    private EmailRequestDTO buildEmailRequest(String to, String subject, String body) {
        return EmailRequestDTO.builder()
                .to(to)
                .subject(subject)
                .body(body)
                .html(true)
                .build();

    }

    private String getRecipientName(UserEmailDTO recipient) {
        if (recipient == null) { return "User"; }

        String firstName = recipient.getFname();

        if (firstName == null || firstName.isBlank())
        { return "User"; }

        return firstName;
    }

    private String buildHtml(String heading, String greeting, String description, TaskEventMessage event, Long previousAssignedToId, Long currentAssignedToId ) {
        StringBuilder html = new StringBuilder();
        html.append("""
                <!DOCTYPE html> 
                <html> 
                <head> 
                <meta charset="UTF-8"> 
                <meta name="viewport" content="width=device-width, initial-scale=1.0"> 
                <title>Office Management System</title> 
                </head> 
                <body style=" margin: 0; padding: 0; background-color: #f4f6f8; font-family: Arial, Helvetica, sans-serif; "> 
                
                <table width="100%" cellpadding="0" cellspacing="0" style="padding: 30px 0;"> 
                <tr> <td align="center"> 
                <table width="600" cellpadding="0" cellspacing="0" style=" background-color: #ffffff; border-radius: 8px; overflow: hidden; border: 1px solid #e1e5e9; "> 
                <tr> <td style=" padding: 22px 30px; background-color: #1f2937; color: #ffffff; "> 
                <h2 style=" margin: 0; font-size: 22px; "> """)
                .append(APP_NAME)
                .append(""" 
                        </h2> 
                        </td> 
                        </tr> <tr> 
                        <td style="padding: 30px;"> <h1 style=" margin-top: 0; color: #1f2937; font-size: 24px; "> 
                        """)
                .append(escapeHtml(heading))
                .append(""" 
                        </h1> 
                        <p style=" color: #374151; font-size: 16px; line-height: 1.6; "> 
                        """)
                .append(escapeHtml(greeting))
                .append(""" 
                        </p> <p style=" color: #4b5563; font-size: 15px; line-height: 1.6; "> 
                        """)
                .append(escapeHtml(description))
                .append(""" 
                        </p> 
                        <table width="100%" cellpadding="0" cellspacing="0" style=" margin-top: 25px; border-collapse: collapse; "> 
                        <tr> 
                        <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; font-weight: bold; color: #374151; "> Task ID </td> 
                        <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; color: #4b5563; "> 
                        """)
                .append(event.getTaskId())
                .append(""" 
                        </td> 
                        </tr> <tr> 
                        <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; font-weight: bold; color: #374151; "> Task </td> 
                        <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; color: #4b5563; "> 
                        """)
                .append(escapeHtml(event.getTitle()))
                .append(""" 
                        </td> </tr> 
                        """);

        if (event.getCurrentStatus() != null)
        {
            html.append(""" 
                    <tr>
                    <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; font-weight: bold; color: #374151; "> Current Status </td> 
                    <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; color: #4b5563; "> 
                    """)
                    .append(escapeHtml(event.getCurrentStatus())) .append(""" 
                            </td> 
                            </tr> 
                            """);
        }

        if (event.getPreviousStatus() != null) {
            html.append(""" 
                    <tr> 
                    <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; font-weight: bold; color: #374151; "> Previous Status </td> 
                    <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; color: #4b5563; "> 
                    """)
                    .append(escapeHtml(event.getPreviousStatus()))
                    .append(""" 
                            </td> 
                            </tr> 
                            """);
        }

        if (previousAssignedToId != null || currentAssignedToId != null) {
            if (previousAssignedToId != null) {
                html.append(""" 
                        <tr> 
                        <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; font-weight: bold; color: #374151; "> Previous Assignee ID </td> 
                        <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; color: #4b5563; ">
                         """)
                        .append(previousAssignedToId)
                        .append("""
                                </td> 
                                </tr> 
                                """);
            }

            if (currentAssignedToId != null) {
                html.append(""" 
                        <tr>
                        <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; font-weight: bold; color: #374151; "> Current Assignee ID </td> 
                        <td style=" padding: 10px; border-bottom: 1px solid #e5e7eb; color: #4b5563; "> 
                        """)
                        .append(currentAssignedToId)
                        .append(""" 
                                </td> </tr> 
                                """);
            }
        }

        html.append("""
                 </table> 
                <p style=" margin-top: 30px; color: #6b7280; font-size: 13px; line-height: 1.5; "> This is an automated notification. Please do not reply directly to this email. </p> 
                </td> 
                </tr> <tr> 
                <td style=" padding: 18px 30px; background-color: #f9fafb; color: #6b7280; font-size: 12px; text-align: center; "> © 2026 Office Management System </td> 
                </tr> 
                </table> 
                </td> 
                </tr> 
                </table> 
                </body> 
                </html> 
                """);
        return html.toString();
    }

    private String escapeHtml(String value) {

        if (value == null) { return ""; }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

}