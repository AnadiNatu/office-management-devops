import type { AppRole } from '../types/auth';

export const PERMISSIONS = {
  TASK_CREATE: ['EMPLOYEE', 'MANAGER', 'ADMIN'],
  TASK_VIEW_ALL: ['MANAGER', 'ADMIN'],
  TASK_ASSIGN: ['MANAGER', 'ADMIN'],
  TASK_REASSIGN: ['MANAGER', 'ADMIN'],
  TASK_DELETE: ['MANAGER', 'ADMIN'],
  MANAGEMENT_PROVISION_USER: ['MANAGER', 'ADMIN'],
  ADMIN_PROVISION_USER: ['ADMIN'],
  ADMIN_CHANGE_ROLE: ['ADMIN'],
  ADMIN_TOGGLE_USER: ['ADMIN'],
  ACCESS_MANAGEMENT: ['MANAGER', 'ADMIN'],
  ACCESS_ADMIN: ['ADMIN'],
} as const satisfies Record<string, readonly AppRole[]>;

export type Permission = keyof typeof PERMISSIONS;

export const can = (role: AppRole | undefined, permission: Permission): boolean =>
  !!role && (PERMISSIONS[permission] as readonly AppRole[]).includes(role);