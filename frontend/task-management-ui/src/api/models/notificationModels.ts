export type NotificationCategory =
  | "TASK"
  | "COMMENT"
  | "MESSAGE"
  | "SYSTEM";



export type NotificationType = 
  | "TASK_CREATED"
  | "TASK_ASSIGNED"
  | "TASK_REASSIGNED"
  | "TASK_STATUS_UPDATED"
  | "TASK_COMPLETED"
  | "TASK_DELETED"
  | "MESSAGE_RECEIVED"
  | "COMMENT_ADDED";

export interface NotificationResponseDTO {
  id: number;

  recipientUserId: number;

  type: NotificationType;

  title?: string | null;

  message: string;

  taskId?: number | null;

  eventId?: string | number | null;

  read: boolean;

  createdAt: string;

  readAt?: string | null;
}

export interface NotificationPageResponseDTO {
  content: NotificationResponseDTO[];

  page: number;
  size: number;

  totalElements: number;
  totalPages: number;

  first?: boolean;
  last?: boolean;
  empty?: boolean;
}

export interface NotificationUnreadCountResponseDTO {
  count: number;
}