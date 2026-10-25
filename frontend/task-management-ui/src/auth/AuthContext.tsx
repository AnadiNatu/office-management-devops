import { createContext } from 'react';
import type { AuthStatus, AuthUser } from '../types/auth';
import type { LoginRequest } from '../api/models/authModels';

export interface AuthContextValue {
  user: AuthUser | null;
  status: AuthStatus;
  isAuthenticated: boolean;
  login: (request: LoginRequest) => Promise<AuthUser>;
  logout: () => void;
  refreshUser: () => Promise<AuthUser | null>;
  updateUser: (patch: Partial<AuthUser>) => void;
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);