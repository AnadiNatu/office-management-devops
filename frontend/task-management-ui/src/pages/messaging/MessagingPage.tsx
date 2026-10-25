import { MessagesSquare } from "lucide-react";
import { useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import EmptyState from "../../components/common/EmptyState";
import LoadingState from "../../components/common/LoadingState";
import PageHeader from "../../components/common/PageHeader";
import ConnectionStatusDot from "../../components/message/ConnectionStatusDot";
import ConversationList from "../../components/message/ConversationList";
import MessageComposer from "../../components/message/MessageComposer";
import MessageList from "../../components/message/MessageList";
import NewConversationModal from "../../components/message/NewConversationModal";
// import { useAuth } from "../../hooks/useAuth";
import { useDisclosure } from "../../hooks/useCommon";
import { useMessagingConnection, useConversations, useConversationMessages } from "../../hooks/useMessaging";
import { ROUTES } from "../../routes/routeConstants";
import { useToast } from "../../ui/Toast";
import  { getErrorMessage } from "../../utils/errorUtils";
import ErrorState from "../../components/common/ErrorState";


const MessagingPage: React.FC = () => {
  const {
    conversationId,
  } = useParams();

  const navigate =
    useNavigate();

  const toast =
    useToast();

  const wsState =
    useMessagingConnection();

  const {
    conversations,
  loading,
  createConversation,
  } = useConversations();

  const activeId =
    conversationId
      ? Number(conversationId)
      : undefined;

  const {
    messages,
    loading: messagesLoading,
    error: messagesError,
    sendMessage,
    markRead,
  } =
    useConversationMessages(
      activeId
    );

  const newModal =
    useDisclosure();
    
  useEffect(() => {
    if (
      conversationId &&
      (
        !Number.isInteger(activeId) ||
        activeId! <= 0
      )
    ) {
      navigate(
        ROUTES.MESSAGES,
        { replace: true }
      );
    }
  }, [
    conversationId,
    activeId,
    navigate,
  ]);

  useEffect(() => {
    if (
      !conversationId &&
      conversations.length > 0
    ) {
      navigate(
        ROUTES.CONVERSATION(
          conversations[0].id
        ),
        { replace: true }
      );
    }
  }, [
    conversationId,
    conversations,
    navigate,
  ]);

  const activeConversation =
    conversations.find(
      (conversation) =>
        conversation.id === activeId
    );

  const otherLabel =
    activeConversation
      ? `Conversation #${activeConversation.id}`
      : undefined;

  const handleCreate = async (
    participantId: number
  ) => {
    try {
      const created =
        await createConversation(
          participantId
        );

      navigate(
        ROUTES.CONVERSATION(
          created.id
        )
      );

      toast.success(
        'Conversation started.'
      );
    } catch (error) {
      toast.error(
        getErrorMessage(
          error,
          'Could not start conversation'
        )
      );

      throw error;
    }
  };

  const handleSend = async (
    content: string
  ) => {
    try {
      await sendMessage(content);
    } catch (error) {
      toast.error(
        getErrorMessage(
          error,
          'Could not send message'
        )
      );
    }
  };

  return (
    <div className="space-y-6 pb-16">

      <PageHeader
        title="Messages"
        subtitle="Direct conversations with your team"
        actions={
          <ConnectionStatusDot
            state={wsState}
          />
        }
      />

      <div className="glass-panel grid h-[70vh] min-h-[32rem] grid-cols-1 overflow-hidden rounded-[2.5rem] md:grid-cols-[20rem_1fr]">

        <div className="border-b border-slate-200/70 p-4 md:border-b-0 md:border-r">
          {loading ? (
            <LoadingState
              variant="skeleton"
              rows={4}
            />
          ) : (
            <ConversationList
  conversations={conversations}
  activeId={activeId}
  onSelect={(id) =>
    navigate(
      ROUTES.CONVERSATION(id)
    )
  }
  onNew={newModal.open}
/>
          )}
        </div>

        <div className="flex min-h-0 flex-col">

          {!activeId ? (
            <EmptyState
              className="m-auto"
              icon={
                <MessagesSquare
                  size={48}
                />
              }
              title="Select a conversation"
              description="Choose a conversation from the list, or start a new one."
            />
          ) : messagesLoading ? 
          (<LoadingState label="Loading messages…"/>) 
          : messagesError ? (<ErrorState message={messagesError}
              onRetry={() => {
                window.location.reload();}} />
          ) : (
            <>
              <div className="border-b border-slate-100 px-5 py-4">
                <p className="text-sm font-black uppercase text-slate-900">
                  {otherLabel}
                </p>
              </div>

              <MessageList
                messages={messages}
                onVisibleUnread={
                  markRead
                }
              />

              <MessageComposer
                onSend={
                  handleSend
                }
              />
            </>
          )}
        </div>
      </div>

      <NewConversationModal
        open={
          newModal.isOpen
        }
        onClose={
          newModal.close
        }
        onCreate={
          handleCreate
        }
      />
    </div>
  );
};

export default MessagingPage;