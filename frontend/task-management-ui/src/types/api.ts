export interface ApiError {
  status: number;
  message: string;
  fieldErrors?: Record<string, string>;
  isNetworkError: boolean;
  raw?: unknown;
}

export const isApiError = (e: unknown): e is ApiError =>
  typeof e === 'object' && e !== null && 'status' in e && 'message' in e && 'isNetworkError' in e;