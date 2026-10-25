package com.office_management.email_service.service;


import com.office_management.email_service.dto.EmailRequestDTO;

public interface MailService {
    void sendEmail(EmailRequestDTO emailRequest);
}