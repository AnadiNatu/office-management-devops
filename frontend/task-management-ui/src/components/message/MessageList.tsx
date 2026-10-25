import { MessageSquare } from "lucide-react";
import { useRef, useEffect } from "react";
import type { MessageResponseDTO } from "../../api/models/messageModel";
import { useAuth } from "../../hooks/useAuth";
import EmptyState from "../common/EmptyState";
import MessageBubble from "./MessageBubble";

interface MessageListProps {
  messages: MessageResponseDTO[];
  onVisibleUnread?: (
    messageId: number
  ) => void;
}

export const MessageList: React.FC<
  MessageListProps
> = ({
  messages,
  onVisibleUnread,
}) => {
  const { user } = useAuth();

  const bottomRef =
    useRef<HTMLDivElement>(null);

  const markedRef =
    useRef<Set<number>>(
      new Set()
    );

  useEffect(() => {
    bottomRef.current?.scrollIntoView({
      behavior: 'smooth',
    });
  }, [messages.length]);

  useEffect(() => {
    if (!onVisibleUnread) {
      return;
    }

    messages.forEach(
      (message) => {
        if (
          message.senderUserId !==
            user?.id &&
          !message.readAt &&
          !markedRef.current.has(
            message.id
          )
        ) {
          markedRef.current.add(
            message.id
          );

          onVisibleUnread(
            message.id
          );
        }
      }
    );
  }, [
    messages,
    onVisibleUnread,
    user?.id,
  ]);

  if (!messages.length) {
    return (
      <EmptyState
        icon={
          <MessageSquare
            size={40}
          />
        }
        title="No messages yet"
        description="Say hello!"
      />
    );
  }

  return (
    <div className="custom-scrollbar flex-1 space-y-3 overflow-y-auto p-4">
      {messages.map(
        (message) => (
          <MessageBubble
            key={message.id}
            message={message}
            isOwn={
              message.senderUserId ===
              user?.id
            }
          />
        )
      )}

      <div ref={bottomRef} />
    </div>
  );
};

export default MessageList;