import { Pencil, Trash2, X, Check } from "lucide-react";
import  { useState } from "react";
import type { CommentResponseDTO } from "../../api/models/commentModels";
import { useAuth } from "../../hooks/useAuth";
import Avatar from "../../ui/Avatar";
import Button from "../../ui/Button";
import TextArea from "../../ui/TextArea";
import { formatRelative } from "../../utils/dateUtils";

interface CommentItemProps {
  comment: CommentResponseDTO;

  onUpdate: (
    commentId: number,
    content: string
  ) => Promise<void>;

  onDelete: (
    commentId: number
  ) => Promise<void>;

  taskTitle?: string;
}

export const CommentItem: React.FC<
  CommentItemProps
> = ({
  comment,
  onUpdate,
  onDelete,
  taskTitle,
}) => {

  const { user } =
    useAuth();

  const isOwner =
    user?.id ===
    comment.commentedByUserId;

  const [editing, setEditing] =
    useState(false);

  const [value, setValue] =
    useState(comment.comment);

  const [saving, setSaving] =
    useState(false);

  const [deleting, setDeleting] =
    useState(false);

  const handleSave =
    async () => {

      const trimmed =
        value.trim();

      if (!trimmed) {
        return;
      }

      setSaving(true);

      try {

        await onUpdate(
          comment.id,
          trimmed
        );

        setEditing(false);

      } finally {

        setSaving(false);
      }
    };

  const handleDelete =
    async () => {

      setDeleting(true);

      try {

        await onDelete(
          comment.id
        );

      } finally {

        setDeleting(false);
      }
    };

  const commenterName =
    `${comment.commenterFirstName ?? ""} ${comment.commenterLastName ?? ""}`
      .trim() || "User";

  return (
    <div
      className="
        flex gap-3 rounded-2xl
        border border-slate-100
        bg-white/60 p-4
      "
    >

      <Avatar
        name={commenterName}
        size="sm"
      />

      <div className="min-w-0 flex-1">

        <div
          className="
            flex flex-wrap
            items-center
            justify-between
            gap-2
          "
        >

          <div
            className="
              flex flex-wrap
              items-center gap-2
            "
          >

            <span
              className="
                text-xs font-black
                uppercase text-slate-900
              "
            >
              {commenterName}
            </span>

            {comment.commenterRole && (
              <span
                className="
                  rounded-md
                  bg-slate-100
                  px-1.5 py-0.5
                  text-[9px]
                  font-black
                  uppercase
                  text-slate-500
                "
              >
                {comment.commenterRole}
              </span>
            )}

            <span
              className="
                text-[10px]
                font-bold
                text-slate-400
              "
            >
              {formatRelative(
                comment.createdAt
              )}
            </span>

          </div>

          {isOwner && !editing && (

            <div className="flex gap-1">

              <button
                type="button"
                onClick={() => {
                  setValue(
                    comment.comment
                  );
                  setEditing(true);
                }}
                aria-label="Edit comment"
                className="
                  rounded-lg p-1.5
                  text-slate-400
                  hover:bg-slate-900/5
                  hover:text-indigo-600
                "
              >
                <Pencil size={14} />
              </button>

              <button
                type="button"
                onClick={
                  handleDelete
                }
                disabled={deleting}
                aria-label="Delete comment"
                className="
                  rounded-lg p-1.5
                  text-slate-400
                  hover:bg-red-50
                  hover:text-red-600
                "
              >
                <Trash2 size={14} />
              </button>

            </div>
          )}

        </div>

        {taskTitle && (

          <p
            className="
              mt-0.5
              text-[10px]
              font-black
              uppercase
              tracking-widest
              text-indigo-500
            "
          >
            On: {taskTitle}
          </p>
        )}

        {editing ? (

          <div className="mt-2 space-y-2">

            <TextArea
              value={value}
              onChange={(e) =>
                setValue(e.target.value)
              }
              rows={2}
              maxLength={5000}
              showCount
            />

            <div
              className="
                flex justify-end gap-2
              "
            >

              <Button
                type="button"
                size="sm"
                variant="ghost"
                leftIcon={
                  <X size={14} />
                }
                onClick={() => {
                  setEditing(false);
                  setValue(
                    comment.comment
                  );
                }}
              >
                Cancel
              </Button>

              <Button
                type="button"
                size="sm"
                leftIcon={
                  <Check size={14} />
                }
                loading={saving}
                disabled={
                  saving ||
                  !value.trim()
                }
                onClick={
                  handleSave
                }
              >
                Save
              </Button>

            </div>

          </div>

        ) : (

          <p
            className="
              mt-1.5
              whitespace-pre-wrap
              text-sm
              font-semibold
              text-slate-700
            "
          >
            {comment.comment}
          </p>

        )}

      </div>

    </div>
  );
};

export default CommentItem;