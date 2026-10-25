// ============================================================
// PAGINATION
// ============================================================

export interface PageResponseDTO<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// ============================================================
// CONVERSATIONS
// ============================================================

export type ConversationType =
  | "ONE_TO_ONE"
  | "GROUP";

export interface CreateConversationRequest {
  participantUserId: number;
}

export interface ConversationSummaryDTO {
  id: number;
  type: ConversationType;
  createdAt?: string;
  updatedAt?: string;
}

export interface ConversationResponseDTO {
  id: number;
  type: ConversationType;

  // Backend calls this participantUserIds
  participantUserIds: number[];

  createdAt?: string;
  updatedAt?: string;

  // Optional because current backend ConversationResponseDTO
  // does not return this field.
  lastMessage?: MessageResponseDTO | null;
}

// ============================================================
// MESSAGES
// ============================================================

export type MessageStatus =
  | "SENT"
  | "DELIVERED"
  | "READ";

export interface MessageCreateRequestDTO {
  content: string;
}

export interface WebSocketMessageRequestDTO {
  content: string;
}

export interface MessageResponseDTO {
  id: number;
  conversationId: number;
  senderUserId: number;
  recipientUserId?: number | null;

  content: string;

  status: MessageStatus;

  createdAt?: string;

  deliveredAt?: string | null;
  readAt?: string | null;
}

// The backend REST/WebSocket status response is also
// MessageResponseDTO.
export type MessageStatusResponseDTO =
  MessageResponseDTO;