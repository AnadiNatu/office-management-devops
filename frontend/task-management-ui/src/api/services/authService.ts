import { AUTH_ENDPOINTS } from "../endpoints/authEndpoints";
import  { apiService } from "../http/apiService";
import type { SignupRequest, SignupResponse, LoginRequest, LoginResponse, RefreshTokenRequest, RefreshTokenResponse, UserResponseDTO, ProvisionUserRequest, SendVerificationOtpRequest, OtpResponse, VerifyEmailRequest, OtpVerifyResponse, OAuthUserInfo, ChangePasswordRequest, ChangePasswordResponse, ForgotPasswordRequest, ForgotPasswordResponse, ResetPasswordRequest, ResetPasswordResponse, SendPhoneOtpRequest, VerifyPhoneOtpRequest, ProfilePhotoResponse, UploadProfilePhotoResponse, DeleteProfilePhotoResponse, UserRole} from "../models/authModels";

export const authService = {

  // ============================================================
  // AUTH
  // ============================================================
  signup: (request: SignupRequest) =>
    apiService.post<SignupResponse>(
      AUTH_ENDPOINTS.SIGNUP,
      request
    ),

  login: (request: LoginRequest) =>
    apiService.post<LoginResponse>(
      AUTH_ENDPOINTS.LOGIN,
      request
    ),

  refresh: (request: RefreshTokenRequest) =>
    apiService.post<RefreshTokenResponse>(
      AUTH_ENDPOINTS.REFRESH,
      request
    ),

  getCurrentUser: () =>
    apiService.get<UserResponseDTO>(
      AUTH_ENDPOINTS.ME
    ),

  provisionUser: (request: ProvisionUserRequest) =>
    apiService.post<UserResponseDTO>(
      AUTH_ENDPOINTS.PROVISION_USER,
      request
    ),

 changeRole: (
  userId: number,
  role: UserRole
) =>
  apiService.put<UserResponseDTO>(
    `${AUTH_ENDPOINTS.CHANGE_ROLE(userId)}?role=${encodeURIComponent(role)}`,
    undefined
  ),
  
  test: () =>
    apiService.get<string>(
      AUTH_ENDPOINTS.TEST
    ),


  // ============================================================
  // ADMIN
  // ============================================================

  disableUser: (userId: number) =>
    apiService.put<UserResponseDTO>(
      AUTH_ENDPOINTS.ADMIN_DISABLE_USER(userId)
    ),

  enableUser: (userId: number) =>
    apiService.put<UserResponseDTO>(
      AUTH_ENDPOINTS.ADMIN_ENABLE_USER(userId)
    ),

  // ============================================================
  // INTERNAL
  // ============================================================

  getUserById: (userId: number) =>
    apiService.get<UserResponseDTO>(
      AUTH_ENDPOINTS.INTERNAL_USER_BY_ID(userId)
    ),

  userExists: (userId: number) =>
    apiService.get<boolean>(
      AUTH_ENDPOINTS.INTERNAL_USER_EXISTS(userId)
    ),


  // ============================================================
  // EMAIL VERIFICATION
  // ============================================================

  sendVerificationOtp: (
    request: SendVerificationOtpRequest
  ) =>
    apiService.post<OtpResponse>(
      AUTH_ENDPOINTS.SEND_EMAIL_VERIFICATION_OTP,
      request
    ),

  verifyEmail: (
    request: VerifyEmailRequest
  ) =>
    apiService.post<OtpVerifyResponse>(
      AUTH_ENDPOINTS.VERIFY_EMAIL,
      request
    ),


  // ============================================================
  // MANAGEMENT
  // ============================================================

  managementProvisionUser: (
    request: ProvisionUserRequest
  ) =>
    apiService.post<UserResponseDTO>(
      AUTH_ENDPOINTS.MANAGEMENT_PROVISION_USER,
      request
    ),

    
getAllUsers: () =>
    apiService.get<UserResponseDTO[]>(
      AUTH_ENDPOINTS.MANAGEMENT_PROVISION_USER
    ),

    getAllUsersByRole: (role: string  ) =>
    apiService.get<UserResponseDTO[]>(
      AUTH_ENDPOINTS.MANAGEMENT_GET_USERS_BY_ROLE(role)
    ),


  // ============================================================
  // OAUTH
  // ============================================================

  getOAuthUserInfo: () =>
    apiService.get<OAuthUserInfo>(
      AUTH_ENDPOINTS.OAUTH2_USER_INFO
    ),

  getOAuthFailure: () =>
    apiService.get<unknown>(
      AUTH_ENDPOINTS.OAUTH2_FAILURE
    ),


  // ============================================================
  // PASSWORD
  // ============================================================

  changePassword: (
    request: ChangePasswordRequest
  ) =>
    apiService.post<ChangePasswordResponse>(
      AUTH_ENDPOINTS.PASSWORD_CHANGE,
      request
    ),

  forgotPassword: (
    request: ForgotPasswordRequest
  ) =>
    apiService.post<ForgotPasswordResponse>(
      AUTH_ENDPOINTS.PASSWORD_FORGOT,
      request
    ),

  resetPassword: (
    request: ResetPasswordRequest
  ) =>
    apiService.post<ResetPasswordResponse>(
      AUTH_ENDPOINTS.PASSWORD_RESET,
      request
    ),


  // ============================================================
  // PASSWORD RESET CONTROLLER
  // ============================================================

  passwordResetForgot: (
    request: ForgotPasswordRequest
  ) =>
    apiService.post<ForgotPasswordResponse>(
      AUTH_ENDPOINTS.PASSWORD_RESET_FORGOT,
      request
    ),

  passwordResetReset: (
    request: ResetPasswordRequest
  ) =>
    apiService.post<ResetPasswordResponse>(
      AUTH_ENDPOINTS.PASSWORD_RESET_RESET,
      request
    ),


  // ============================================================
  // PHONE OTP
  // ============================================================

  sendPhoneOtp: (
    request: SendPhoneOtpRequest
  ) =>
    apiService.post<OtpResponse>(
      AUTH_ENDPOINTS.PHONE_SEND_OTP,
      request
    ),

  verifyPhoneOtp: (
    request: VerifyPhoneOtpRequest
  ) =>
    apiService.post<OtpVerifyResponse>(
      AUTH_ENDPOINTS.PHONE_VERIFY_OTP,
      request
    ),


  // ============================================================
  // PROFILE PHOTO
  // ============================================================

  getProfilePhoto: () =>
    apiService.get<ProfilePhotoResponse>(
      AUTH_ENDPOINTS.PROFILE_PHOTO
    ),

  uploadProfilePhoto: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiService.upload<UploadProfilePhotoResponse>(AUTH_ENDPOINTS.PROFILE_PHOTO,formData,'POST');
  },

  removeProfilePhoto: () =>
    apiService.delete<DeleteProfilePhotoResponse>(
      AUTH_ENDPOINTS.PROFILE_PHOTO
    ),

};