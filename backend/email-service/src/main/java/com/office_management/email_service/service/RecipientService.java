package com.office_management.email_service.service;


import com.office_management.email_service.dto.UserEmailDTO;

public interface RecipientService {
    UserEmailDTO getUserById(Long userId);
    String getEmailAddress(Long userId);
}