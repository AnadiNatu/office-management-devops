package com.office_management.auth_service.service;

import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.exception.errors.UserNotFoundException;
import com.office_management.auth_service.repository.UserRepository;
import com.office_management.auth_service.service.interfaces.EmailVerificationService;
import com.office_management.auth_service.service.interfaces.NotificationService;
import com.office_management.auth_service.service.interfaces.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final NotificationService notificationService;

    public void sendVerificationOtp(String email) {
        Users user = userRepository.findByEmailIgnoreCase(email.trim()).orElseThrow(() -> new UserNotFoundException("User not found"));
        String otp = otpService.generateAndStore("EMAIL_VERIFICATION:" + user.getEmail().toLowerCase());
        notificationService.sendOtp(user.getEmail(), user.getFname(), otp, "EMAIL_VERIFICATION");
    }

    public void verifyEmail(String email, String otp) {
        Users user = userRepository.findByEmailIgnoreCase(email.trim()).orElseThrow(() -> new UserNotFoundException("User not found"));
        otpService.verify("EMAIL_VERIFICATION:" + user.getEmail().toLowerCase(), otp);
        user.setEmailVerified(true);
        userRepository.save(user);
    }

}