import { Send } from "lucide-react";
import  { useState } from "react";
import Button from "../../ui/Button";



interface MessageComposerProps {
  onSend: (content: string) => Promise<void> | void;
  disabled?: boolean;
}

export const MessageComposer: React.FC<MessageComposerProps> = ({ onSend, disabled }) => {
  const [value, setValue] = useState('');
  const [sending, setSending] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const content = value.trim();
    if (!content) return;
    setSending(true);
    setValue('');
    try {
      await onSend(content);
    } finally {
      setSending(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex items-end gap-3 border-t border-slate-100 bg-white/60 p-4">
      <textarea
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSubmit(e);
          }
        }}
        placeholder="Type a message…"
        rows={1}
        disabled={disabled}
        className="max-h-32 flex-1 resize-none rounded-2xl border-2 border-slate-200 bg-white p-3 text-sm font-semibold outline-none focus:border-transparent focus:ring-2 focus:ring-indigo-600 disabled:opacity-60"
      />
      <Button type="submit" size="md" loading={sending} disabled={disabled || !value.trim()} leftIcon={<Send size={16} />}>
        Send
      </Button>
    </form>
  );
};

export default MessageComposer;