package com.office_management.auth_service.config.otp;

public class OtpConstants {
    private OtpConstants() {
    }

    public static final int OTP_LENGTH = 6;

    public static final long OTP_EXPIRATION_MINUTES = 5;

    public static final int MAX_ATTEMPTS = 5;

    public static final long RESEND_COOLDOWN_SECONDS = 60;

    public static final String EMAIL_VERIFICATION = "EMAIL_VERIFICATION";
    public static final String PASSWORD_RESET = "PASSWORD_RESET";
    public static final String PHONE_VERIFICATION = "PHONE_VERIFICATION";
    public static final String OTP_LOGIN = "OTP_LOGIN";
}