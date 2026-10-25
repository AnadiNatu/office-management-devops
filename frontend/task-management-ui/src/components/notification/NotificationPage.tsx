import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Bell, CheckCheck } from "lucide-react";

import type {
  NotificationResponseDTO,
  NotificationCategory,
} from "../../api/models/notificationModels";

import { notificationService } from "../../api";

import Button from "../../ui/Button";
import { cn } from "../../utils/formatUtils";

import EmptyState from "../common/EmptyState";
import ErrorState from "../common/ErrorState";
import LoadingState from "../common/LoadingState";
import PageHeader from "../common/PageHeader";
import NotificationItem from "./NotificationItem";
import { NOTIFICATION_TYPES } from "./NotificationTypeIcon";

const NotificationsPage: React.FC = () => {
  const [notifications, setNotifications] =
    useState<NotificationResponseDTO[]>([]);

  const [activeType, setActiveType] =
    useState<NotificationCategory | null>(null);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState<string | null>(null);

  const [markingAllRead, setMarkingAllRead] =
    useState(false);

  /*
   * ============================================================
   * LOAD NOTIFICATIONS
   * ============================================================
   */

  const loadNotifications = useCallback(
    async () => {
      setLoading(true);
      setError(null);

      try {
        const response =
          await notificationService.getMyNotifications(
            0,
            50
          );

        setNotifications(
          Array.isArray(response?.content)
            ? response.content
            : []
        );
      } catch (error) {
        setNotifications([]);

        setError(
          error instanceof Error
            ? error.message
            : "Could not load notifications."
        );
      } finally {
        setLoading(false);
      }
    },
    []
  );

  useEffect(() => {
    void loadNotifications();
  }, [loadNotifications]);

  /*
   * ============================================================
   * FILTER
   * ============================================================
   */

  const list = useMemo(() => {
    if (!activeType) {
      return notifications;
    }

    return notifications.filter(
      (notification) => {
        const type =
          notification.type
            ?.trim()
            .toUpperCase() ?? "";

        switch (activeType) {
          case "TASK":
            return type.startsWith("TASK");

          case "COMMENT":
            return type.startsWith("COMMENT");

          case "MESSAGE":
            return type.startsWith("MESSAGE");

          case "SYSTEM":
            return (
              type.startsWith("SYSTEM") ||
              type === "GENERAL" ||
              type === "INFO"
            );

          default:
            return false;
        }
      }
    );
  }, [
    activeType,
    notifications,
  ]);

  /*
   * ============================================================
   * MARK ONE AS READ
   * ============================================================
   */

  const handleClick = async (
    notification: NotificationResponseDTO
  ) => {
    if (notification.read) {
      return;
    }

    try {
      const updated =
        await notificationService.markAsRead(
          notification.id
        );

      setNotifications((previous) =>
        previous.map((item) =>
          item.id === notification.id
            ? {
                ...item,
                ...updated,
                read: true,
              }
            : item
        )
      );
    } catch (error) {
      setError(
        error instanceof Error
          ? error.message
          : "Could not mark notification as read."
      );
    }
  };

  /*
   * ============================================================
   * MARK ALL AS READ
   * ============================================================
   */

  const handleMarkAllRead = async () => {
    const unreadExists =
      notifications.some(
        (notification) =>
          !notification.read
      );

    if (!unreadExists || markingAllRead) {
      return;
    }

    setMarkingAllRead(true);
    setError(null);

    try {
      await notificationService.markAllAsRead();

      /*
       * Immediately update the local UI.
       */
      setNotifications((previous) =>
        previous.map(
          (notification) => ({
            ...notification,
            read: true,
            readAt:
              notification.readAt ??
              new Date().toISOString(),
          })
        )
      );
    } catch (error) {
      setError(
        error instanceof Error
          ? error.message
          : "Could not mark all notifications as read."
      );
    } finally {
      setMarkingAllRead(false);
    }
  };

  /*
   * ============================================================
   * RETRY
   * ============================================================
   */

  const handleRetry = () => {
    void loadNotifications();
  };

  /*
   * ============================================================
   * RENDER
   * ============================================================
   */

  const hasUnread =
    notifications.some(
      (notification) =>
        !notification.read
    );

  return (
    <div className="space-y-8 pb-16">

      <PageHeader
        title="Notifications"
        subtitle="Everything that needs your attention"
        actions={
          <Button
            size="sm"
            variant="secondary"
            leftIcon={
              <CheckCheck size={14} />
            }
            onClick={handleMarkAllRead}
            disabled={
              !hasUnread ||
              markingAllRead
            }
          >
            {markingAllRead
              ? "Marking..."
              : "Mark all read"}
          </Button>
        }
      />

      <div className="flex flex-wrap gap-2">

        <button
          type="button"
          onClick={() =>
            setActiveType(null)
          }
          className={cn(
            "rounded-xl px-4 py-2 text-[10px] font-black uppercase tracking-widest transition-colors",
            !activeType
              ? "bg-slate-950 text-white"
              : "bg-slate-100 text-slate-600 hover:bg-slate-200"
          )}
        >
          All
        </button>

        {NOTIFICATION_TYPES.map(
          (type) => (
            <button
              key={type}
              type="button"
              onClick={() =>
                setActiveType(
                  type as NotificationCategory
                )
              }
              className={cn(
                "rounded-xl px-4 py-2 text-[10px] font-black uppercase tracking-widest transition-colors",
                activeType === type
                  ? "bg-indigo-600 text-white"
                  : "bg-slate-100 text-slate-600 hover:bg-slate-200"
              )}
            >
              {type}
            </button>
          )
        )}
      </div>

      {loading ? (
        <LoadingState
          variant="skeleton"
          rows={5}
        />
      ) : error ? (
        <ErrorState
          message={error}
          onRetry={handleRetry}
        />
      ) : list.length === 0 ? (
        <EmptyState
          icon={
            <Bell size={48} />
          }
          title="No notifications"
          description={
            activeType
              ? `There are no ${activeType.toLowerCase()} notifications.`
              : "Nothing here yet."
          }
        />
      ) : (
        <div className="glass-panel space-y-1 rounded-[2.5rem] p-3">

          {list.map(
            (notification) => (
              <NotificationItem
                key={notification.id}
                notification={
                  notification
                }
                onClick={() => {
                  void handleClick(
                    notification
                  );
                }}
              />
            )
          )}

        </div>
      )}
    </div>
  );
};

export default NotificationsPage;