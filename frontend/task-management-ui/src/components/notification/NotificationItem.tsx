import type { NotificationResponseDTO } from "../../api/models/notificationModels";
import { formatRelative } from "../../utils/dateUtils";
import { cn } from "../../utils/formatUtils";
import { NotificationTypeIcon } from "./NotificationTypeIcon";


interface NotificationItemProps {
  notification: NotificationResponseDTO;
  onClick?: () => void;
}

function formatNotificationType(
  type: string
): string {
  return type
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) =>
      letter.toUpperCase()
    );
}

export const NotificationItem: React.FC<
  NotificationItemProps
> = ({
  notification,
  onClick,
}) => (
  <button
    type="button"
    onClick={onClick}
    className={cn(
      "flex w-full items-start gap-3 rounded-2xl p-4 text-left transition-colors",
      notification.read
        ? "bg-transparent hover:bg-slate-900/5"
        : "bg-indigo-50/70 hover:bg-indigo-50"
    )}
  >
    <NotificationTypeIcon
      type={notification.type}
    />

    <div className="min-w-0 flex-1">

      <div className="flex items-start justify-between gap-2">

        <p className="text-xs font-black uppercase text-slate-900">
          {notification.title ??
            formatNotificationType(
              notification.type
            )}
        </p>

        {!notification.read && (
          <span className="mt-1 h-2 w-2 shrink-0 rounded-full bg-indigo-600" />
        )}

      </div>

      <p className="mt-1 text-xs font-semibold text-slate-600">
        {notification.message}
      </p>

      <p className="mt-1.5 text-[9px] font-bold uppercase tracking-widest text-slate-400">
        {formatRelative(
          notification.createdAt
        )}
      </p>

    </div>
  </button>
);

export default NotificationItem;