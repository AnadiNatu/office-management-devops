/**
 * Production-safe API configuration.
 *
 * In production the UI is served from the same origin as the gateway,
 * therefore an empty base URL makes calls such as /api/auth/... stay
 * on the current HTTPS domain.
 *
 * For local Vite development you can set:
 * VITE_API_BASE_URL=http://localhost:8080
 */
export const API_BASE_URL =
  (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/+$/, '');

export const API_CONFIG = {
  baseUrl: API_BASE_URL,
  timeout: 30000,
} as const;
