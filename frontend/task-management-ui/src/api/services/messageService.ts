import { MESSAGE_ENDPOINTS } from "../endpoints/messageEndpoints";
import { apiService } from "../http/apiService";

import type {
  CreateConversationRequest,
  ConversationResponseDTO,
  MessageCreateRequestDTO,
  MessageResponseDTO,
  PageResponseDTO,
  ConversationSummaryDTO,
} from "../models/messageModel";

export const messageService = {
  
  // ============================================================
  // CONVERSATIONS
  // ============================================================

  createConversation: (
    request: CreateConversationRequest
  ): Promise<ConversationResponseDTO> =>
    apiService.post<ConversationResponseDTO>(
      MESSAGE_ENDPOINTS.CREATE_CONVERSATION,
      request
    ),

  getConversation: (
    conversationId: number
  ): Promise<ConversationResponseDTO> =>
    apiService.get<ConversationResponseDTO>(
      MESSAGE_ENDPOINTS.GET_CONVERSATION(
        conversationId
      )
    ),

  getMyConversations: (
    page = 0,
    size = 50
  ): Promise<PageResponseDTO<ConversationSummaryDTO>> =>
    apiService.get<PageResponseDTO<ConversationSummaryDTO>>(
      `${MESSAGE_ENDPOINTS.MY_CONVERSATIONS}?page=${page}&size=${size}`
    ),

  // ============================================================
  // MESSAGES
  // ============================================================

  sendMessage: (
    conversationId: number,
    request: MessageCreateRequestDTO
  ): Promise<MessageResponseDTO> =>
    apiService.post<MessageResponseDTO>(
      MESSAGE_ENDPOINTS.SEND_MESSAGE(
        conversationId
      ),
      request
    ),

  getMessages: (
    conversationId: number,
    page = 0,
    size = 50
  ): Promise<PageResponseDTO<MessageResponseDTO>> =>
    apiService.get<PageResponseDTO<MessageResponseDTO>>(
      `${MESSAGE_ENDPOINTS.GET_MESSAGES(
        conversationId
      )}?page=${page}&size=${size}`
    ),

  // ============================================================
  // STATUS
  // ============================================================

  markAsDelivered: (
    messageId: number
  ): Promise<MessageResponseDTO> =>
    apiService.put<MessageResponseDTO>(
      MESSAGE_ENDPOINTS.MARK_AS_DELIVERED(
        messageId
      )
    ),

  markAsRead: (
    messageId: number
  ): Promise<MessageResponseDTO> =>
    apiService.put<MessageResponseDTO>(
      MESSAGE_ENDPOINTS.MARK_AS_READ(
        messageId
      )
    ),
};

export default messageService;