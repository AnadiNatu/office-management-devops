import { useState, useEffect, useMemo, useCallback, useRef } from "react";
import  { messageWebSocketClient, messageService } from "../api";
import type { ConversationResponseDTO, CreateConversationRequest, MessageResponseDTO } from "../api/models/messageModel";
import  { useAuth } from "./useAuth";
import { useAsync } from "./useCommon";
// import { ensureArray } from "../utils/arraysUtils";

export type WsConnectionState =
  | "disconnected"
  | "connecting"
  | "connected"
  | "error";


// ============================================================
// WEBSOCKET CONNECTION
// ============================================================

export function useMessagingConnection(): WsConnectionState {

  const {
    isAuthenticated,
  } = useAuth();

  const [
    state,
    setState,
  ] = useState<WsConnectionState>(
    messageWebSocketClient.getState()
  );

  useEffect(() => {

    if (!isAuthenticated) {

      messageWebSocketClient.disconnect();

      setState("disconnected");

      return;
    }

    const unsubscribe =
      messageWebSocketClient.onStateChange(
        setState
      );

    messageWebSocketClient.connect();

    return () => {

      unsubscribe();

      messageWebSocketClient.disconnect();
    };

  }, [
    isAuthenticated,
  ]);

  return state;
}


// ============================================================
// CONVERSATIONS
// ============================================================

export function useConversations() {

  const {
    user,
  } = useAuth();

  const {
    data,
    setData,
    loading,
    error,
    reload,
  } =
    useAsync<ConversationResponseDTO[]>(
      async () => {

        const page =
          await messageService.getMyConversations(
            0,
            50
          );

        const summaries =
          Array.isArray(page?.content)
            ? page.content
            : [];

        /*
         * Current backend ConversationSummaryDTO only contains:
         *
         * id
         * type
         * createdAt
         * updatedAt
         *
         * Therefore load the complete conversation
         * to obtain participantUserIds.
         */
        const conversations =
          await Promise.all(
            summaries.map(
              async (summary) => {

                try {

                  const detail =
                    await messageService.getConversation(
                      summary.id
                    );

                  return detail;

                } catch {

                  /*
                   * Do not allow one broken conversation
                   * to destroy the entire messaging page.
                   */
                  return {
                    id: summary.id,
                    type: summary.type,
                    participantUserIds: [],
                    createdAt:
                      summary.createdAt,
                    updatedAt:
                      summary.updatedAt,
                    lastMessage: null,
                  };
                }
              }
            )
          );

        return conversations;
      },
      []
    );


  const conversations =
    useMemo(
      () => {

        const safe =
          Array.isArray(data)
            ? data
            : [];

        return [...safe].sort(
          (a, b) => {

            const aDate =
              a.lastMessage?.createdAt ??
              a.updatedAt ??
              a.createdAt ??
              "";

            const bDate =
              b.lastMessage?.createdAt ??
              b.updatedAt ??
              b.createdAt ??
              "";

            return bDate.localeCompare(
              aDate
            );
          }
        );

      },
      [data]
    );


  // ==========================================================
  // CREATE
  // ==========================================================

  const createConversation =
    useCallback(
      async (
        participantUserId: number
      ) => {

        if (
          !Number.isInteger(
            participantUserId
          ) ||
          participantUserId <= 0
        ) {
          throw new Error(
            "Invalid participant user ID."
          );
        }

        if (
          user?.id != null &&
          participantUserId === user.id
        ) {
          throw new Error(
            "You cannot start a conversation with yourself."
          );
        }

        const request:
          CreateConversationRequest = {
            participantUserId,
          };

        const created =
          await messageService.createConversation(
            request
          );

        setData(
          (previous) => {

            const current =
              Array.isArray(previous)
                ? previous
                : [];

            const exists =
              current.some(
                (conversation) =>
                  conversation.id ===
                  created.id
              );

            if (exists) {
              return current;
            }

            return [
              created,
              ...current,
            ];
          }
        );

        return created;
      },
      [
        setData,
        user?.id,
      ]
    );


  // ==========================================================
  // LIVE MESSAGE → CONVERSATION UPDATE
  // ==========================================================

  const patchLastMessage =
    useCallback(
      (
        message: MessageResponseDTO
      ) => {

        setData(
          (previous) => {

            const current =
              Array.isArray(previous)
                ? previous
                : [];

            return current.map(
              (conversation) => {

                if (
                  conversation.id !==
                  message.conversationId
                ) {
                  return conversation;
                }

                return {
                  ...conversation,

                  lastMessage:
                    message,

                  updatedAt:
                    message.createdAt ??
                    conversation.updatedAt,
                };
              }
            );
          }
        );
      },
      [setData]
    );


  useEffect(
    () =>
      messageWebSocketClient.onMessage(
        patchLastMessage
      ),
    [
      patchLastMessage,
    ]
  );


  return {
    conversations,
    loading,
    error,
    reload,
    createConversation,
    currentUserId:
      user?.id,
  };
}


// ============================================================
// CONVERSATION MESSAGES
// ============================================================

export function useConversationMessages(
  conversationId:
    number | undefined
) {

  const {
    user,
  } = useAuth();

  const [
    messages,
    setMessages,
  ] =
    useState<MessageResponseDTO[]>(
      []
    );

  const [
    loading,
    setLoading,
  ] =
    useState(false);

  const [
    error,
    setError,
  ] =
    useState<string | null>(
      null
    );

  const seenIds =
    useRef<Set<number>>(
      new Set()
    );


  // ==========================================================
  // LOAD
  // ==========================================================

  const load =
    useCallback(
      async () => {

        if (
          conversationId == null
        ) {

          setMessages([]);

          setLoading(false);

          return;
        }

        setLoading(true);

        setError(null);

        try {

          const page =
            await messageService.getMessages(
              conversationId,
              0,
              50
            );

          const loaded =
            Array.isArray(
              page?.content
            )
              ? page.content
              : [];

          seenIds.current =
            new Set(
              loaded.map(
                (message) =>
                  message.id
              )
            );

          setMessages(
            loaded
          );

        } catch (e) {

          setError(
            e instanceof Error
              ? e.message
              : "Could not load messages."
          );

        } finally {

          setLoading(false);
        }

      },
      [
        conversationId,
      ]
    );


  useEffect(
    () => {
      void load();
    },
    [load]
  );


  // ==========================================================
  // INCOMING SOCKET MESSAGE
  // ==========================================================

  useEffect(
    () => {

      if (
        conversationId == null
      ) {
        return;
      }

      return messageWebSocketClient.onMessage(
        async (message) => {

          if (
            message.conversationId !==
            conversationId
          ) {
            return;
          }

          if (
            seenIds.current.has(
              message.id
            )
          ) {
            return;
          }

          seenIds.current.add(
            message.id
          );

          setMessages(
            previous => [
              ...previous,
              message,
            ]
          );

          if (
            message.senderUserId !==
            user?.id
          ) {

            const sent =
              messageWebSocketClient
                .markDelivered(
                  message.id
                );

            if (!sent) {

              try {

                await messageService
                  .markAsDelivered(
                    message.id
                  );

              } catch {
                // Ignore delivery fallback failure.
              }
            }
          }
        }
      );

    },
    [
      conversationId,
      user?.id,
    ]
  );


  // ==========================================================
  // STATUS
  // ==========================================================

  useEffect(
    () => {

      return messageWebSocketClient.onStatus(
        (status) => {

          setMessages(
            previous =>
              previous.map(
                message => {

                  if (
                    message.id !==
                    status.id
                  ) {
                    return message;
                  }

                  return {
                    ...message,
                    status:
                      status.status ??
                      message.status,

                    deliveredAt:
                      status.deliveredAt ??
                      message.deliveredAt,

                    readAt:
                      status.readAt ??
                      message.readAt,
                  };
                }
              )
          );
        }
      );

    },
    []
  );


  // SEND

  const sendMessage =
  useCallback(
    async (content: string) => {

      const trimmed =
        content.trim();


      if (
        conversationId == null ||
        !trimmed
      ) {
        return;
      }


      // --------------------------------------------------------
      // Try WebSocket only if it is REALLY connected.
      // --------------------------------------------------------

      const sent =
        messageWebSocketClient.sendMessage(
          conversationId,
          trimmed
        );


      if (sent) {
        return;
      }


      // --------------------------------------------------------
      // REST FALLBACK
      // --------------------------------------------------------

      const created =
        await messageService.sendMessage(
          conversationId,
          {
            content: trimmed,
          }
        );


      if (
        seenIds.current.has(
          created.id
        )
      ) {
        return;
      }


      seenIds.current.add(
        created.id
      );


      setMessages(
        (previous) => [
          ...previous,
          created,
        ]
      );

    },
    [
      conversationId,
    ]
  );


  // READ
  const markRead =
    useCallback(
      async (
        messageId: number
      ) => {

        setMessages(
          previous =>
            previous.map(
              message =>
                message.id ===
                messageId
                  ? {
                      ...message,
                      status:
                        "READ",
                      readAt:
                        new Date()
                          .toISOString(),
                    }
                  : message
            )
        );

        const sent =
          messageWebSocketClient.markRead(
            messageId
          );

        if (!sent) {

          try {

            const updated =
              await messageService.markAsRead(
                messageId
              );

            setMessages(
              previous =>
                previous.map(
                  message =>
                    message.id ===
                    messageId
                      ? updated
                      : message
                )
            );

          } catch {

            /*
             * Reload if optimistic state
             * could not be persisted.
             */
            await load();
          }
        }

      },
      [load]
    );


  return {
    messages,
    loading,
    error,
    reload: load,
    sendMessage,
    markRead,
  };
}