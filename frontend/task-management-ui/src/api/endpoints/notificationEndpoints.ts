export const NOTIFICATION_ENDPOINTS = {

  MY_NOTIFICATIONS:
  '/api/notifications/my',

  MY_UNREAD:
    '/api/notifications/my/unread',

  MY_UNREAD_COUNT:
    '/api/notifications/my/unread/count',

  BY_ID: (notificationId: number | string) =>
    `/api/notifications/${notificationId}`,

  MARK_AS_READ: (notificationId: number | string) =>
    `/api/notifications/${notificationId}/read`,

  MARK_ALL_AS_READ:
    '/api/notifications/my/read-all',

  BY_TYPE: (type: string) =>
    `/api/notifications/my/type/${encodeURIComponent(type)}`,

} as const;