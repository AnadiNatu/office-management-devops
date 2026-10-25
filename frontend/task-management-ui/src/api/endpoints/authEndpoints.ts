export const AUTH_ENDPOINTS = {
  // AUTH
  SIGNUP: '/api/auth/signup',
  LOGIN: '/api/auth/login',
  REFRESH: '/api/auth/refresh',
  ME: '/api/auth/me',
  PROVISION_USER: '/api/auth/provision',
  CHANGE_ROLE: (userId: number | string) => `/api/auth/${userId}/role`,
TEST: '/api/auth/test',

  // ADMIN
  ADMIN_DISABLE_USER: (userId: number | string) =>`/api/admin/users/${userId}/disable`,
  ADMIN_ENABLE_USER: (userId: number | string) =>`/api/admin/users/${userId}/enable`,

  // INTERNAL
  INTERNAL_USER_BY_ID: (userId: number | string) =>`/api/auth/internal/users/${userId}`,
  INTERNAL_USER_EXISTS: (userId: number | string) =>`/api/auth/internal/users/${userId}/exists`,

  // EMAIL VERIFICATION
  SEND_EMAIL_VERIFICATION_OTP:    '/api/auth/verify-email/send',
  VERIFY_EMAIL:    '/api/auth/verify-email',

  // MANAGEMENT
  MANAGEMENT_PROVISION_USER:'/api/management/users',
  MANAGEMENT_GET_USERS_BY_ROLE: (role: string) => `/api/management/users/roles/${role}`,
  // OAUTH2
  OAUTH2_USER_INFO:'/api/oauth2/user-info',
  OAUTH2_FAILURE:'/api/oauth2/failure',
  // PASSWORD
  PASSWORD_CHANGE:'/api/password/change',
  PASSWORD_FORGOT:'/api/password/forgot',
  PASSWORD_RESET:'/api/password/reset',

  // PASSWORD RESET CONTROLLER
  PASSWORD_RESET_FORGOT: '/api/password-reset/forgot',
  PASSWORD_RESET_RESET: '/api/password-reset/reset',

  // PHONE OTP
  PHONE_SEND_OTP:'/api/otp/phone/send',
  PHONE_VERIFY_OTP:'/api/otp/phone/verify',

  // PROFILE
  PROFILE_PHOTO:'/api/profile/photo',
} as const;