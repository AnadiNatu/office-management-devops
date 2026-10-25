package com.office_management.auth_service.service;

import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.exception.errors.AccountDisabledException;
import com.office_management.auth_service.exception.errors.UserNotFoundException;
import com.office_management.auth_service.repository.UserRepository;
import com.office_management.auth_service.security.JwtUtil;
import com.office_management.auth_service.service.interfaces.OtpLoginService;
import com.office_management.auth_service.service.interfaces.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpLoginServiceImpl implements OtpLoginService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    public void sendEmailOtp(String email) {

        Users user = userRepository.findByEmailIgnoreCase(email.trim()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new AccountDisabledException("Account is disabled");
        }

        String otp = otpService.generateAndStore("OTP_LOGIN:EMAIL:" + user.getEmail().toLowerCase());
    }

    public void verifyEmailOtp(String email, String otp) {
        otpService.verify("OTP_LOGIN:EMAIL:" + email.trim().toLowerCase(), otp);
    }
}