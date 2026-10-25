import { isApiError } from '../types/api';

export const getErrorMessage = (e: unknown, fallback = 'Something went wrong'): string => {
  if (isApiError(e)) return e.message || fallback;
  if (e instanceof Error) return e.message || fallback;
  return fallback;
};