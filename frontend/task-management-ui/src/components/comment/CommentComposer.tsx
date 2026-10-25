import { Send } from "lucide-react";
import  { useState } from "react";
import Button from "../../ui/Button";
import TextArea from "../../ui/TextArea";

interface CommentComposerProps {
  onSubmit: (content: string) => Promise<void>;
  placeholder?: string;
}

export const CommentComposer: React.FC<
  CommentComposerProps
> = ({
  onSubmit,
  placeholder = "Add a comment…",
}) => {

  const [content, setContent] =
    useState("");

  const [submitting, setSubmitting] =
    useState(false);

  const handleSubmit =
    async (
      e: React.FormEvent
    ) => {

      e.preventDefault();

      const trimmed =
        content.trim();

      if (!trimmed) {
        return;
      }

      setSubmitting(true);

      try {

        await onSubmit(trimmed);

        setContent("");

      } finally {

        setSubmitting(false);
      }
    };

  return (
    <form
      onSubmit={handleSubmit}
      className="flex flex-col gap-3"
    >

      <TextArea
        value={content}
        onChange={(e) =>
          setContent(e.target.value)
        }
        placeholder={placeholder}
        rows={3}
        maxLength={5000}
        showCount
      />

      <div className="flex justify-end">

        <Button
          type="submit"
          size="sm"
          loading={submitting}
          disabled={
            submitting ||
            !content.trim()
          }
          leftIcon={
            <Send size={14} />
          }
        >
          Post comment
        </Button>

      </div>

    </form>
  );
};

export default CommentComposer;