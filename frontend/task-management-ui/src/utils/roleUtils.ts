import type { AppRole } from '../types/auth';

export const normalizeRole = (role: string | null):AppRole=> {
  const r = (role ?? '').trim().toUpperCase();
  if (r === 'ADMIN' || r === 'MANAGER' || r === 'EMPLOYEE') return r;
  return 'USER';
};

export const ROLE_LABELS: Record<AppRole, string> = {
  USER: 'User',
  EMPLOYEE: 'Employee',
  MANAGER: 'Manager',
  ADMIN: 'Administrator',
};

export const toBackendRole = (role: AppRole): string => `ROLE_${role}`;

export const hasAnyRole = (role: AppRole | undefined, allowed: AppRole[]): boolean =>
  !!role && allowed.includes(role);