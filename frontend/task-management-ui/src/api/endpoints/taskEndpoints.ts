export const TASK_ENDPOINTS = {

  CREATE:
    '/api/tasks/create',

  GET_BY_ID: (taskId: number | string) =>
    `/api/tasks/${taskId}`,

  MY_TASKS:
    '/api/tasks/my',

  ALL_TASKS:
    '/api/tasks/allTask',

  ASSIGN: (taskId: number | string) =>
    `/api/tasks/${taskId}/assign`,

  REASSIGN: (taskId: number | string) =>
    `/api/tasks/${taskId}/reassign`,

  UPDATE_STATUS: (taskId: number | string) =>
    `/api/tasks/${taskId}/status`,

  UPDATE: (taskId: number | string) =>
    `/api/tasks/${taskId}`,

  DELETE: (taskId: number | string) =>
    `/api/tasks/${taskId}`,

} as const;