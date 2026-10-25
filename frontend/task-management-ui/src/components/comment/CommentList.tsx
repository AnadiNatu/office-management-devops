
import React, { useState } from "react";
import type { CommentResponseDTO } from "../../api/models/commentModels";


interface CommentListProps {
  comments: CommentResponseDTO[];

  onUpdate: (
    id: number,
    comment: string
  ) => Promise<void>;

  onDelete: (
    commentId: number
  ) => Promise<void>;

  taskTitleFor: (
    comment: CommentResponseDTO
  ) => string;
}

const CommentList: React.FC<CommentListProps> = ({
  comments,
  onUpdate,
  onDelete,
  taskTitleFor,
}) => {
  const [editingId, setEditingId] =
    useState<number | null>(null);

  const [editingText, setEditingText] =
    useState<string>("");

  const [savingId, setSavingId] =
    useState<number | null>(null);

  const [deletingId, setDeletingId] =
    useState<number | null>(null);

  const safeComments = Array.isArray(comments)
    ? comments
    : [];

  const startEditing = (
    comment: CommentResponseDTO
  ) => {
    setEditingId(comment.id);
    setEditingText(comment.comment ?? "");
  };

  const cancelEditing = () => {
    setEditingId(null);
    setEditingText("");
  };

  const handleUpdate = async (
    id: number
  ) => {
    const trimmed = editingText.trim();

    if (!trimmed) {
      return;
    }

    try {
      setSavingId(id);

      await onUpdate(id, trimmed);

      setEditingId(null);
      setEditingText("");
    } finally {
      setSavingId(null);
    }
  };

  const handleDelete = async (
    commentId: number
  ) => {
    try {
      setDeletingId(commentId);

      await onDelete(commentId);
    } finally {
      setDeletingId(null);
    }
  };

  if (safeComments.length === 0) {
    return (
      <div className="rounded-3xl border border-dashed border-slate-200 bg-white/50 p-12 text-center">
        <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-slate-100">
          <span className="text-2xl">💬</span>
        </div>

        <h3 className="text-sm font-black uppercase tracking-widest text-slate-500">
          No comments
        </h3>

        <p className="mt-2 text-sm font-semibold text-slate-400">
          You haven't posted any comments yet.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {safeComments.map((comment) => {
        const isEditing =
          editingId === comment.id;

        const isSaving =
          savingId === comment.id;

        const isDeleting =
          deletingId === comment.id;

        return (
          <article
            key={comment.id}
            className="
              rounded-3xl
              border
              border-slate-200/80
              bg-white
              p-5
              shadow-sm
              transition
              hover:shadow-md
            "
          >
            {/* Header */}
            <div
              className="
                mb-4
                flex
                items-start
                justify-between
                gap-4
              "
            >
              <div className="min-w-0">
                <p
                  className="
                    text-[10px]
                    font-black
                    uppercase
                    tracking-widest
                    text-indigo-500
                  "
                >
                  {taskTitleFor(comment)}
                </p>

                <p
                  className="
                    mt-1
                    text-[11px]
                    font-semibold
                    text-slate-400
                  "
                >
                  {comment.createdAt
                    ? new Date(
                        comment.createdAt
                      ).toLocaleString()
                    : ""}
                </p>
              </div>

              <span
                className="
                  shrink-0
                  rounded-full
                  bg-slate-100
                  px-3
                  py-1
                  text-[10px]
                  font-black
                  uppercase
                  tracking-wider
                  text-slate-500
                "
              >
                Task #{comment.taskId}
              </span>
            </div>

            {/* Comment content */}
            {isEditing ? (
              <div className="space-y-3">
                <textarea
                  value={editingText}
                  onChange={(event) =>
                    setEditingText(
                      event.target.value
                    )
                  }
                  rows={4}
                  className="
                    w-full
                    resize-none
                    rounded-2xl
                    border
                    border-slate-200
                    bg-slate-50
                    px-4
                    py-3
                    text-sm
                    font-semibold
                    text-slate-800
                    outline-none
                    transition
                    focus:border-indigo-400
                    focus:ring-2
                    focus:ring-indigo-100
                  "
                />

                <div className="flex justify-end gap-2">
                  <button
                    type="button"
                    onClick={cancelEditing}
                    disabled={isSaving}
                    className="
                      rounded-xl
                      px-4
                      py-2
                      text-xs
                      font-black
                      uppercase
                      tracking-wider
                      text-slate-500
                      transition
                      hover:bg-slate-100
                      disabled:cursor-not-allowed
                      disabled:opacity-50
                    "
                  >
                    Cancel
                  </button>

                  <button
                    type="button"
                    onClick={() =>
                      handleUpdate(comment.id)
                    }
                    disabled={
                      isSaving ||
                      !editingText.trim()
                    }
                    className="
                      rounded-xl
                      bg-indigo-600
                      px-4
                      py-2
                      text-xs
                      font-black
                      uppercase
                      tracking-wider
                      text-white
                      transition
                      hover:bg-indigo-700
                      disabled:cursor-not-allowed
                      disabled:opacity-50
                    "
                  >
                    {isSaving
                      ? "Saving..."
                      : "Save"}
                  </button>
                </div>
              </div>
            ) : (
              <>
                <p
                  className="
                    whitespace-pre-wrap
                    text-sm
                    font-semibold
                    leading-6
                    text-slate-700
                  "
                >
                  {comment.comment}
                </p>

                {/* Actions */}
                <div
                  className="
                    mt-5
                    flex
                    items-center
                    justify-end
                    gap-2
                    border-t
                    border-slate-100
                    pt-4
                  "
                >
                  <button
                    type="button"
                    onClick={() =>
                      startEditing(comment)
                    }
                    disabled={isDeleting}
                    className="
                      rounded-xl
                      px-4
                      py-2
                      text-xs
                      font-black
                      uppercase
                      tracking-wider
                      text-indigo-600
                      transition
                      hover:bg-indigo-50
                      disabled:cursor-not-allowed
                      disabled:opacity-50
                    "
                  >
                    Edit
                  </button>

                  <button
                    type="button"
                    onClick={() =>
                      handleDelete(comment.id)
                    }
                    disabled={isDeleting}
                    className="
                      rounded-xl
                      px-4
                      py-2
                      text-xs
                      font-black
                      uppercase
                      tracking-wider
                      text-red-600
                      transition
                      hover:bg-red-50
                      disabled:cursor-not-allowed
                      disabled:opacity-50
                    "
                  >
                    {isDeleting
                      ? "Deleting..."
                      : "Delete"}
                  </button>
                </div>
              </>
            )}
          </article>
        );
      })}
    </div>
  );
};

export default CommentList;