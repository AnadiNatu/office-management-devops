export const COMMENT_ENDPOINTS = {

  ADD_COMMENT: (taskId: number | string) =>
    `/api/comments/task/${taskId}`,

  GET_COMMENTS_BY_TASK: (taskId: number | string) =>
    `/api/comments/task/${taskId}`,

  GET_COMMENT_BY_ID: (commentId: number | string) =>
    `/api/comments/${commentId}`,

  MY_COMMENTS:
    '/api/comments/my',

  UPDATE_COMMENT: (commentId: number | string) =>
    `/api/comments/${commentId}`,

  DELETE_COMMENT: (commentId: number | string) =>
    `/api/comments/${commentId}`,

} as const;