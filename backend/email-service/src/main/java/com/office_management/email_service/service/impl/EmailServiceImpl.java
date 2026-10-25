package com.office_management.email_service.service.impl;


import com.office_management.email_service.dto.EmailRequestDTO;
import com.office_management.email_service.exceptions.errors.EmailSendingException;
import com.office_management.email_service.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Override public void sendEmail(EmailRequestDTO emailRequest) {
        if (emailRequest == null) {
            throw new EmailSendingException( "Email request cannot be null" );
        }

        if (emailRequest.getTo() == null || emailRequest.getTo().isBlank()) {
            throw new EmailSendingException( "Recipient email address is required" );
        }

        if (emailRequest.getSubject() == null || emailRequest.getSubject().isBlank()) {
            throw new EmailSendingException( "Email subject is required" );
        }

        if (emailRequest.getBody() == null || emailRequest.getBody().isBlank()) {
            throw new EmailSendingException( "Email body is required" );
        }
        try
        {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper( message, true, "UTF-8" );
            helper.setFrom(fromAddress);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText( emailRequest.getBody(), emailRequest.isHtml());
            mailSender.send(message);
            log.info( "Email sent successfully to {} with subject '{}'", emailRequest.getTo(), emailRequest.getSubject() );
        } catch (MessagingException | MailException exception) {
            log.error( "Failed to send email to {} with subject '{}'", emailRequest.getTo(), emailRequest.getSubject(), exception );
            throw new EmailSendingException( "Failed to send email", exception );
        }
    }
}