import type { ApiError } from '../../types/api';
import type { AxiosError, AxiosInstance } from 'axios';
export const toApiError = (error: AxiosError<any>): ApiError => {
  if (!error.response) {
    return {
      status: 0,
      message: error.code === 'ECONNABORTED'
        ? 'Request timed out. Please try again.'
        : 'Unable to reach the server. Check your connection.',
      isNetworkError: true,
      raw: error,
    };
  }
  const { status, data } = error.response;
  const message =
    (typeof data === 'string' && data) ||
    data?.message ||
    data?.error ||
    (status === 403 ? 'You do not have permission to do that.' :
     status === 404 ? 'The requested resource was not found.' :
     status >= 500 ? 'Server error. Please try again later.' :
     'Request failed.');
  const fieldErrors: Record<string, string> | undefined =
    data?.errors && typeof data.errors === 'object' && !Array.isArray(data.errors)
      ? data.errors
      : undefined;
  return { status, message, fieldErrors, isNetworkError: false, raw: data };
};

export const attachErrorInterceptor = (instance: AxiosInstance) => {
  instance.interceptors.response.use(
    (res) => res,
    (error: AxiosError) => Promise.reject(toApiError(error))
  );
};