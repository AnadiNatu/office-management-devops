import { CheckSquare, MessagesSquare, MessageSquare, Bell } from "lucide-react";
import type { NotificationCategory } from "../../api/models/notificationModels";

export const NOTIFICATION_TYPES: NotificationCategory[] = [
  "TASK",
  "COMMENT",
  "MESSAGE",
  "SYSTEM",
];

const ICONS: Record<
  NotificationCategory,
  React.ReactNode
> = {
  TASK: <CheckSquare size={16} />,
  COMMENT: <MessagesSquare size={16} />,
  MESSAGE: <MessageSquare size={16} />,
  SYSTEM: <Bell size={16} />,
};

const COLORS: Record<
  NotificationCategory,
  string
> = {
  TASK:
    "bg-indigo-100 text-indigo-700",

  COMMENT:
    "bg-purple-100 text-purple-700",

  MESSAGE:
    "bg-sky-100 text-sky-700",

  SYSTEM:
    "bg-slate-100 text-slate-700",
};

function resolveCategory(
  type: string
): NotificationCategory {
  const normalized =
    type.trim().toUpperCase();

  if (
    normalized.startsWith("TASK")
  ) {
    return "TASK";
  }

  if (
    normalized.startsWith("COMMENT")
  ) {
    return "COMMENT";
  }

  if (
    normalized.startsWith("MESSAGE")
  ) {
    return "MESSAGE";
  }

  return "SYSTEM";
}

export const NotificationTypeIcon: React.FC<{
  type: string;
}> = ({ type }) => {
  const category =
    resolveCategory(type);

  return (
    <div
      className={`rounded-xl p-2.5 ${COLORS[category]}`}
    >
      {ICONS[category]}
    </div>
  );
};

export default NotificationTypeIcon;