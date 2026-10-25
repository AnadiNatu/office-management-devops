export const ROUTES = {
  LOGIN: '/login',
  SIGNUP: '/signup',
  FORGOT_PASSWORD: '/forgot-password',
  RESET_PASSWORD: '/reset-password',
  VERIFY_EMAIL: '/verify-email',
  OAUTH_CALLBACK: '/oauth2/callback',
  OAUTH_FAILURE: '/oauth2/failure',

  DASHBOARD: '/',
  TASKS: '/tasks',
  TASK_DETAIL: (id: number | string = ':id') => `/tasks/${id}`,
  COMMENTS: '/comments',
  MESSAGES: '/messages',
  CONVERSATION: (id: number | string = ':conversationId') => `/messages/${id}`,
  NOTIFICATIONS: '/notifications',
  PROFILE: '/profile',

  MANAGEMENT: '/management',
  ADMIN: '/admin',

  FORBIDDEN: '/403',
  NOT_FOUND: '*',
} as const;