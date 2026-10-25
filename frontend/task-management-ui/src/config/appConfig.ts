export const APP_CONFIG = {
  name: 'OfficeHub',
  storageKeys: {
    accessToken: 'oms.accessToken',
    refreshToken: 'oms.refreshToken',
    user: 'oms.user',
  },
  defaultPageSize: 10,
  toastDurationMs: 4000,
} as const;