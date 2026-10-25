import { Plus, MessagesSquare } from "lucide-react";
import type { ConversationResponseDTO } from "../../api/models/messageModel";

import { useAuth } from "../../hooks/useAuth";
import { useUserDirectory } from "../../hooks/useCommon";
import Avatar from "../../ui/Avatar";
import Button from "../../ui/Button";
import { formatRelative } from "../../utils/dateUtils";
import { cn, truncate } from "../../utils/formatUtils";
import EmptyState from "../common/EmptyState";

interface ConversationListProps {
  conversations:
    ConversationResponseDTO[];

  activeId?: number;

  onSelect:
    (id: number) => void;

  onNew:
    () => void;

  className?: string;
}


export const ConversationList:
  React.FC<ConversationListProps> =
  ({
    conversations,
    activeId,
    onSelect,
    onNew,
    className,
  }) => {

    const {
      user,
    } = useAuth();

    const {
      nameOf,
    } = useUserDirectory();


    const safeConversations =
      Array.isArray(
        conversations
      )
        ? conversations
        : [];


    const otherParticipant =
      (
        conversation:
          ConversationResponseDTO
      ): number | undefined => {

        const ids =
          Array.isArray(
            conversation.participantUserIds
          )
            ? conversation.participantUserIds
            : [];

        return ids.find(
          id =>
            id !== user?.id
        );
      };


    return (
      <div
        className={cn(
          "flex flex-col",
          className
        )}
      >

        <div
          className="
            flex
            items-center
            justify-between
            gap-2
            px-2
            pb-4
          "
        >

          <h3
            className="
              text-[10px]
              font-black
              uppercase
              tracking-widest
              text-slate-400
            "
          >
            Conversations
          </h3>

          <Button
            size="sm"
            variant="glass"
            leftIcon={
              <Plus size={14} />
            }
            onClick={onNew}
          >
            New
          </Button>

        </div>


        {safeConversations.length === 0 ? (

          <EmptyState
            icon={
              <MessagesSquare
                size={40}
              />
            }
            title="No conversations"
            description="Start a new one."
          />

        ) : (

          <div
            className="
              custom-scrollbar
              flex-1
              space-y-1.5
              overflow-y-auto
            "
          >

            {safeConversations.map(
              conversation => {

                const participantId =
                  otherParticipant(
                    conversation
                  );

                const label =
                  participantId != null
                    ? nameOf(
                        participantId,
                        `User ${participantId}`
                      )
                    : `Conversation #${conversation.id}`;

                const isActive =
                  conversation.id ===
                  activeId;


                return (
                  <button
                    key={
                      conversation.id
                    }
                    type="button"
                    onClick={() =>
                      onSelect(
                        conversation.id
                      )
                    }
                    className={cn(
                      `
                        flex
                        w-full
                        items-center
                        gap-3
                        rounded-2xl
                        p-3
                        text-left
                        transition-colors
                      `,
                      isActive
                        ? `
                          bg-indigo-600
                          text-white
                        `
                        : `
                          hover:bg-slate-900/5
                        `
                    )}
                  >

                    <Avatar
                      name={label}
                      size="sm"
                    />

                    <div
                      className="
                        min-w-0
                        flex-1
                      "
                    >

                      <div
                        className="
                          flex
                          items-center
                          justify-between
                          gap-2
                        "
                      >

                        <p
                          className={cn(
                            `
                              truncate
                              text-xs
                              font-black
                              uppercase
                            `,
                            isActive
                              ? "text-white"
                              : "text-slate-900"
                          )}
                        >
                          {label}
                        </p>

                        {conversation.updatedAt && (
                          <span
                            className={cn(
                              `
                                shrink-0
                                text-[9px]
                                font-bold
                              `,
                              isActive
                                ? "text-indigo-100"
                                : "text-slate-400"
                            )}
                          >
                            {formatRelative(
                              conversation.updatedAt
                            )}
                          </span>
                        )}

                      </div>


                      <p
                        className={cn(
                          `
                            truncate
                            text-[11px]
                            font-semibold
                          `,
                          isActive
                            ? "text-indigo-100"
                            : "text-slate-500"
                        )}
                      >
                        {
                          conversation.lastMessage
                            ? truncate(
                                conversation.lastMessage.content,
                                50
                              )
                            : "No messages yet"
                        }
                      </p>

                    </div>

                  </button>
                );
              }
            )}

          </div>
        )}

      </div>
    );
  };


export default ConversationList;