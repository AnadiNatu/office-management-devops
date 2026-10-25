export const MESSAGE_ENDPOINTS = {
  CREATE_CONVERSATION: '/api/conversations/create-convo',

  GET_CONVERSATION: (conversationId: number | string) =>
    `/api/conversations/${conversationId}`,

  MY_CONVERSATIONS: '/api/conversations/my',

  SEND_MESSAGE: (conversationId: number | string) =>
    `/api/conversations/${conversationId}/messages`,

  GET_MESSAGES: (conversationId: number | string) =>
    `/api/conversations/${conversationId}/messages`,

  MARK_AS_DELIVERED: (messageId: number | string) =>
    `/api/messages/${messageId}/delivered`,

  MARK_AS_READ: (messageId: number | string) =>
    `/api/messages/${messageId}/read`,
} as const;

export const MESSAGE_WS_ENDPOINTS = {
  SEND: '/messages.send',
  DELIVERED: '/messages.delivered',
  READ: '/messages.read',
} as const;