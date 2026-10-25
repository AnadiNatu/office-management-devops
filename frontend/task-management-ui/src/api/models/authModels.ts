// USER
export type UserRole =
  | "ADMIN"
  | "MANAGER"
  | "EMPLOYEE";


export interface UserResponseDTO {
  id: number;
  fname: string;
  lname: string;
  email: string;
  phoneNumber?: string | null;
  role: string;
  profilePicture?: string | null;
  enabled: boolean;
  emailVerified: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// SIGNUP
export interface SignupRequest {
  fname: string;
  lname: string;
  email: string;
  password: string;
  phoneNumber?: string;
  role?: string;
}

export interface SignupResponse {
  message?: string;
  user?: UserResponseDTO;
}

// LOGIN
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  userId: number;
  username: string;
  email: string;
  fname: string;
  lname: string;
  role: string;
  profilePicture?: string | null;
  message?: string;
}

// REFRESH TOKEN
export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RefreshTokenResponse {
  token: string;
  refreshToken?: string;
}

// PROVISION USER
export interface ProvisionUserRequest {
  fname: string;
  lname: string;
  email: string;
  password?: string;
  phoneNumber?: string;
  role: UserRole;
}

export interface ChangeRoleRequest {
  role: UserRole;
}

// EMAIL VERIFICATION
export interface SendVerificationOtpRequest {
  email: string;
}

export interface VerifyEmailRequest {
  email: string;
  otp: string;
}

// export interface OtpRequest {
//   phoneNumber?: string;
// }

export interface OtpResponse {
  message?: string;
  success?: boolean;
}

export interface OtpVerifyResponse {
  message?: string;
  success?: boolean;
}

// PASSWORD
export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface ChangePasswordResponse {
  message?: string;
  success?: boolean;
}

export interface ForgotPasswordRequest {
  email?: string;
  method?: string;
}

export interface ForgotPasswordResponse {
  message?: string;
  success?: boolean;
}

export interface ResetPasswordRequest {
  identifier: string;
  otp: string;
  newPassword: string;
}

export interface ResetPasswordResponse {
  message?: string;
  success?: boolean;
}
// PHONE OTP
export interface SendPhoneOtpRequest {
  phoneNumber: string;
}

export interface VerifyPhoneOtpRequest {
  identifier: string;
  otp: string;
}

// PROFILE
// export interface ProfilePhotoResponse {
//   profilePicture?: string;
//   url?: string;
//   message?: string;
// }

// export interface UploadProfilePhotoResponse {
//   profilePicture?: string;
//   url?: string;
//   message?: string;
// }

// export interface DeleteProfilePhotoResponse {
//   message?: string;
//   success?: boolean;
// }

export interface ProfilePhotoResponse {
  photoUrl: string;
  userType: string;
}

export interface UploadProfilePhotoResponse {
  message: string;
  photoUrl: string;
  userType: string;
}

export interface DeleteProfilePhotoResponse {
  message: string;
}


// OAUTH
export interface OAuthUserInfo {
  id?: number;
  email?: string;
  name?: string;
  fname?: string;
  lname?: string;
  profilePicture?: string;
  role?: string;
}