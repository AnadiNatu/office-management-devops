package com.office_management.auth_service.service.interfaces;

import com.office_management.auth_service.dto.ResetPasswordRequest;

public interface PasswordResetService {

    public void requestReset(String email);
    public void resetPassword(ResetPasswordRequest request);

}