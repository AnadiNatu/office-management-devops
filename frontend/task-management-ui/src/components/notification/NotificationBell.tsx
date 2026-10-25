import  { Bell } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../routes/routeConstants";
import Dropdown, { DropdownDivider } from "../../ui/Dropdown";
import { formatCount } from "../../utils/formatUtils";
import EmptyState from "../common/EmptyState";
import LoadingState from "../common/LoadingState";
import { useNotifications, useUnreadCount } from "../../hooks/useNotification";
import NotificationItem from "./NotificationItem";

export const NotificationBell: React.FC =
  () => {
    const navigate =
      useNavigate();

    const {
      count,
      refresh,
    } = useUnreadCount();

    const {
      notifications,
      loading,
      markAsRead,
      markAllAsRead,
    } = useNotifications();

    const recent =
      Array.isArray(notifications)
        ? notifications.slice(0, 6)
        : [];

    const handleItemClick =
      async (
        id: number,
        read?: boolean
      ) => {
        if (read) {
          return;
        }

        try {
          await markAsRead(id);
          await refresh();
        } catch {
          /*
           * The notification hook/service
           * owns the actual error handling.
           */
        }
      };

    const handleMarkAllRead =
      async () => {
        if (count <= 0) {
          return;
        }

        try {
          await markAllAsRead();
          await refresh();
        } catch {
          /*
           * Do not break the dropdown if
           * the request fails.
           */
        }
      };

    return (
      <Dropdown
        trigger={({
          toggle,
          open,
        }) => (
          <button
            type="button"
            onClick={toggle}
            aria-haspopup="menu"
            aria-expanded={open}
            aria-label={`Notifications${
              count
                ? `, ${count} unread`
                : ""
            }`}
            className="relative rounded-2xl p-2.5 text-slate-600 transition-colors hover:bg-slate-900/5"
          >
            <Bell size={20} />

            {count > 0 && (
              <span className="absolute -right-1 -top-1 flex h-5 min-w-[1.25rem] items-center justify-center rounded-full bg-red-600 px-1 text-[9px] font-black text-white">
                {formatCount(count)}
              </span>
            )}
          </button>
        )}
        panelClassName="w-80 p-0"
      >
        <div className="flex items-center justify-between border-b border-slate-200/70 px-4 py-3">

          <p className="text-xs font-black uppercase tracking-widest text-slate-900">
            Notifications
          </p>

          {count > 0 && (
            <button
              type="button"
              onClick={() => {
                void handleMarkAllRead();
              }}
              className="text-[10px] font-black uppercase tracking-widest text-indigo-600 hover:text-indigo-700"
            >
              Mark all read
            </button>
          )}

        </div>

        <div className="custom-scrollbar max-h-80 overflow-y-auto p-2">

          {loading ? (
            <LoadingState
              variant="skeleton"
              rows={2}
            />
          ) : recent.length === 0 ? (
            <EmptyState
              icon={<Bell size={32} />}
              title="You're all caught up"
              className="py-10"
            />
          ) : (
            <div className="space-y-1">

              {recent.map(
                (notification) => (
                  <NotificationItem
                    key={
                      notification.id
                    }
                    notification={
                      notification
                    }
                    onClick={() => {
                      void handleItemClick(
                        notification.id,
                        notification.read
                      );
                    }}
                  />
                )
              )}

            </div>
          )}

        </div>

        <DropdownDivider />

        <button
          type="button"
          onClick={() =>
            navigate(
              ROUTES.NOTIFICATIONS
            )
          }
          className="w-full rounded-b-3xl px-4 py-3 text-center text-[10px] font-black uppercase tracking-widest text-indigo-600 hover:bg-indigo-50"
        >
          View all notifications
        </button>
      </Dropdown>
    );
  };

export default NotificationBell;