import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { AuthContext, type AuthContextValue,  } from './AuthContext';
import { authStorage, AUTH_LOGOUT_EVENT } from './authStorage';
import type { AuthStatus, AuthUser } from '../types/auth';
import { normalizeRole } from '../utils/roleUtils';
import { isTokenExpired } from '../utils/tokenUtils';
import type { UserResponseDTO } from '../api/models/authModels';
import  { authService } from '../api/services/authService';

const toAuthUser = (u: UserResponseDTO): AuthUser => ({
  id: u.id,
  fname: u.fname,
  lname: u.lname,
  email: u.email,
  phoneNumber: u.phoneNumber,
  role: normalizeRole(u.role),
  profilePicture: u.profilePicture,
  enabled: u.enabled,
  emailVerified: u.emailVerified,
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthUser | null>(() => authStorage.getUser());
  const [status, setStatus] = useState<AuthStatus>('loading');

  const logout = useCallback(() => {
    authStorage.clear();
    setUser(null);
    setStatus('unauthenticated');
  }, []);

  const refreshUser = useCallback(async () => {
    try {
      const me = await authService.getCurrentUser();
      const next = toAuthUser(me);
      authStorage.setUser(next);
      setUser(next);
      setStatus('authenticated');
      return next;
    } catch {
      return null;
    }
  }, []);

  // Bootstrap session
  useEffect(() => {
    let cancelled = false;
   
  const bootstrap = async () => {
    const access = authStorage.getAccessToken();
    const refresh = authStorage.getRefreshToken();

    if (!access && !refresh) {
      if (!cancelled) {
        setUser(null);
        setStatus('unauthenticated');
      }

      return;
    }

    if (isTokenExpired(access) && !refresh) {
      authStorage.clear();

      if (!cancelled) {
        setUser(null);
        setStatus('unauthenticated');
      }

      return;
    }

    const me = await refreshUser();

    if (!cancelled && !me) {
      logout();
    }
  };

  void bootstrap();

  return () => {
    cancelled = true;
  };
}, [refreshUser, logout]);

  // Forced logout from the HTTP layer
  useEffect(() => {
    window.addEventListener(AUTH_LOGOUT_EVENT, logout);
    return () => window.removeEventListener(AUTH_LOGOUT_EVENT, logout);
  }, [logout]);

  const login: AuthContextValue['login'] = useCallback(async (request) => {
    const res = await authService.login(request);
    authStorage.setTokens(res.token, res.refreshToken);
    const base: AuthUser = {
      id: res.userId,
      fname: res.fname,
      lname: res.lname,
      email: res.email,
      role: normalizeRole(res.role),
      profilePicture: res.profilePicture,
      enabled: true,
      emailVerified: false,
    };
    authStorage.setUser(base);
    setUser(base);
    setStatus('authenticated');
    // Enrich with full profile (emailVerified, phone, ...)
    const full = await refreshUser();
    return full ?? base;
  }, [refreshUser]);

  const updateUser = useCallback((patch: Partial<AuthUser>) => {
    setUser((prev) => {
      if (!prev) return prev;
      const next = { ...prev, ...patch };
      authStorage.setUser(next);
      return next;
    });
  }, []);

  const value = useMemo<AuthContextValue>(
  () => ({
    user,
    status,
    isAuthenticated:
      status === 'authenticated' && !!user,
    login,
    logout,
    refreshUser,
    updateUser,
  }),
  [
    user,
    status,
    login,
    logout,
    refreshUser,
    updateUser,
  ]
);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};