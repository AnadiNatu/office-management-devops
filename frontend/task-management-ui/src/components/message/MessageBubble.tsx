import { CheckCheck, Check } from "lucide-react";
import type { MessageResponseDTO } from "../../api/models/messageModel";
import { formatTime } from "../../utils/dateUtils";
import { cn } from "../../utils/formatUtils";

interface MessageBubbleProps {
  message: MessageResponseDTO;
  isOwn: boolean;
}

export const MessageBubble: React.FC<
  MessageBubbleProps
> = ({
  message,
  isOwn,
}) => (
  <div
    className={cn(
      'flex',
      isOwn
        ? 'justify-end'
        : 'justify-start'
    )}
  >
    <div
      className={cn(
        'max-w-[75%] rounded-[1.5rem] px-4 py-3 text-sm font-semibold shadow-sm',
        isOwn
          ? 'rounded-br-md bg-indigo-600 text-white'
          : 'rounded-bl-md border border-slate-100 bg-white text-slate-800'
      )}
    >
      <p className="whitespace-pre-wrap break-words">
        {message.content}
      </p>

      <div
        className={cn(
          'mt-1.5 flex items-center justify-end gap-1 text-[9px] font-bold',
          isOwn
            ? 'text-indigo-100'
            : 'text-slate-400'
        )}
      >
        {message.createdAt
          ? formatTime(
              message.createdAt
            )
          : ''}

        {isOwn &&
          (message.readAt ? (
            <CheckCheck
              size={13}
            />
          ) : message.deliveredAt ? (
            <Check size={13} />
          ) : null)}
      </div>
    </div>
  </div>
);

export default MessageBubble;