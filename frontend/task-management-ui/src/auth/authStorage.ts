import { APP_CONFIG } from '../config/appConfig';
import type { AuthUser } from '../types/auth';

const { accessToken, refreshToken, user } = APP_CONFIG.storageKeys;

const safeGet = (k: string) => {
  try { return localStorage.getItem(k); } catch { return null; }
};
const safeSet = (k: string, v: string) => {
  try { localStorage.setItem(k, v); } catch { /* ignore */ }
};
const safeRemove = (k: string) => {
  try { localStorage.removeItem(k); } catch { /* ignore */ }
};

export const authStorage = {
  getAccessToken: () => safeGet(accessToken),
  getRefreshToken: () => safeGet(refreshToken),
  setTokens: (access: string, refresh?: string | null) => {
    safeSet(accessToken, access);
    if (refresh) safeSet(refreshToken, refresh);
  },
  getUser: (): AuthUser | null => {
    const raw = safeGet(user);
    if (!raw) return null;
    try { return JSON.parse(raw) as AuthUser; } catch { return null; }
  },
  setUser: (u: AuthUser) => safeSet(user, JSON.stringify(u)),
  clear: () => {
    safeRemove(accessToken);
    safeRemove(refreshToken);
    safeRemove(user);
  },
};

export const AUTH_LOGOUT_EVENT = 'oms:auth-logout';