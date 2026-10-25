import { useMemo } from "react";
import CommentList from "../../components/comment/CommentList";
import ErrorState from "../../components/common/ErrorState";
import LoadingState from "../../components/common/LoadingState";
import PageHeader from "../../components/common/PageHeader";
import { useMyComments } from "../../hooks/useComments";
const MyCommentsPage: React.FC = () => {
  const {
    comments,
    loading,
    error,
    reload,
    updateComment,
    deleteComment,
  } = useMyComments();

  const sorted = useMemo(
    () =>
      [...comments].sort(
        (a, b) =>
          (b.createdAt ?? "").localeCompare(
            a.createdAt ?? ""
          )
      ),
    [comments]
  );

  return (
    <div className="space-y-8 pb-16">
      <PageHeader
        title="My Comments"
        subtitle="Every comment you've posted across tasks"
      />

      {loading ? (
        <LoadingState
          variant="skeleton"
          rows={4}
        />
      ) : error ? (
        <ErrorState
          message={error}
          onRetry={reload}
        />
      ) : (
        <CommentList
          comments={sorted}
          onUpdate={async (id, comment) => {
            await updateComment(id, { comment });
          }}
          onDelete={deleteComment}
          taskTitleFor={(c) => `Task #${c.taskId}`}
        />
      )}
    </div>
  );
};

export default MyCommentsPage;