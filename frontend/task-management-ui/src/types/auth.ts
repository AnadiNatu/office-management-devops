export type AppRole = 'USER' | 'EMPLOYEE' | 'MANAGER' | 'ADMIN';

export interface AuthUser {
  id: number;
  fname: string;
  lname: string;
  email: string;
  phoneNumber?: string | null;
  role: AppRole;
  profilePicture?: string | null;
  enabled: boolean;
  emailVerified: boolean;
}

export type AuthStatus = 'loading' | 'authenticated' | 'unauthenticated';