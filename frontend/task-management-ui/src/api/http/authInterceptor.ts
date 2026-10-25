import { API_CONFIG } from '../../config/apiConfig';
import { AUTH_ENDPOINTS } from '../endpoints/authEndpoints';
import { authStorage, AUTH_LOGOUT_EVENT } from '../../auth/authStorage';
import axios, { type InternalAxiosRequestConfig, type AxiosInstance, AxiosError } from 'axios';
import type { RefreshTokenResponse } from '../models/authModels';

const PUBLIC_PATHS: string[] = [
  AUTH_ENDPOINTS.LOGIN,
  AUTH_ENDPOINTS.SIGNUP,
  AUTH_ENDPOINTS.REFRESH,
  AUTH_ENDPOINTS.PASSWORD_FORGOT,
  AUTH_ENDPOINTS.PASSWORD_RESET,
  AUTH_ENDPOINTS.PASSWORD_RESET_FORGOT,
  AUTH_ENDPOINTS.PASSWORD_RESET_RESET,
];

const isPublic = (url?: string) => !!url && PUBLIC_PATHS.some((p) => url.startsWith(p));

let refreshPromise: Promise<string | null> | null = null;

const refreshAccessToken = async (): Promise<string | null> => {
  const refreshToken = authStorage.getRefreshToken();
  if (!refreshToken) return null;
  try {
    // Plain axios: avoids interceptor recursion
    const { data } = await axios.post<RefreshTokenResponse>(
      `${API_CONFIG.baseUrl}${AUTH_ENDPOINTS.REFRESH}`,
      { refreshToken },
      { timeout: API_CONFIG.timeout }
    );
    authStorage.setTokens(data.token, data.refreshToken ?? refreshToken);
    return data.token;
  } catch {
    return null;
  }
};

type RetriableConfig = InternalAxiosRequestConfig & { _retry?: boolean };

export const attachAuthInterceptor = (instance: AxiosInstance) => {
  instance.interceptors.request.use((config) => {
    const token = authStorage.getAccessToken();
    if (token && !isPublic(config.url)) {
      config.headers.set('Authorization', `Bearer ${token}`);
    }
    return config;
  });

  instance.interceptors.response.use(
    (res) => res,
    async (error: AxiosError) => {
      const original = error.config as RetriableConfig | undefined;
      if (error.response?.status !== 401 || !original || original._retry || isPublic(original.url)) {
        return Promise.reject(error);
      }
      original._retry = true;
      refreshPromise ??= refreshAccessToken().finally(() => { refreshPromise = null; });
      const newToken = await refreshPromise;

      if (!newToken) {
        authStorage.clear();
        window.dispatchEvent(new Event(AUTH_LOGOUT_EVENT));
        return Promise.reject(error);
      }
      original.headers.set('Authorization', `Bearer ${newToken}`);
      return instance(original);
    }
  );
};