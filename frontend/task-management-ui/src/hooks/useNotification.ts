import {
  useState,
  useCallback,
  useEffect,
  useMemo,
  useRef,
} from "react";

import { notificationService } from "../api";
import type {
  NotificationPageResponseDTO,
  NotificationResponseDTO,
} from "../api/models/notificationModels";

import { useAuth } from "./useAuth";
import { useAsync } from "./useCommon";

const POLL_MS = 30_000;

/**
 * ============================================================
 * UNREAD COUNT
 * ============================================================
 */
export function useUnreadCount() {
  const { isAuthenticated } = useAuth();

  const [count, setCount] = useState<number>(0);

  const timer = useRef<number | null>(null);

  const refresh = useCallback(async () => {
    if (!isAuthenticated) {
      setCount(0);
      return;
    }

    try {
      const response = await notificationService.getUnreadCount();

      /*
       * Backend DTO:
       *
       * {
       *   unreadCount: number
       * }
       */
      setCount(response);
    } catch {
      /*
       * Do not break the UI because of a background
       * unread-count request failure.
       */
    }
  }, [isAuthenticated]);

  useEffect(() => {
    if (!isAuthenticated) {
      setCount(0);
      return;
    }

    void refresh();

    timer.current = window.setInterval(() => {
      void refresh();
    }, POLL_MS);

    return () => {
      if (timer.current !== null) {
        window.clearInterval(timer.current);
        timer.current = null;
      }
    };
  }, [isAuthenticated, refresh]);

  return {
    count,
    refresh,
    setCount,
  };
}

/**
 * ============================================================
 * NOTIFICATIONS
 * ============================================================
 */
export function useNotifications() {
  const {
    data,
    setData,
    loading,
    error,
    reload,
  } = useAsync<NotificationResponseDTO[]>(
    async () => {
      const response: NotificationPageResponseDTO =
        await notificationService.getMyNotifications(0, 50);

      return response?.content ?? [];
    },
    []
  );

  /**
   * Keep notifications sorted newest first.
   */
  const notifications = useMemo<NotificationResponseDTO[]>(
    () =>
      [...(data ?? [])].sort((a, b) =>
        (b.createdAt ?? "").localeCompare(a.createdAt ?? "")
      ),
    [data]
  );

  /**
   * Calculate unread count from currently loaded notifications.
   */
  const unreadCount = useMemo(
    () => notifications.filter((notification) => !notification.read).length,
    [notifications]
  );

  /**
   * ==========================================================
   * MARK ONE AS READ
   * ==========================================================
   */
  const markAsRead = useCallback(
    async (notificationId: number): Promise<NotificationResponseDTO> => {
      const updated =
        await notificationService.markAsRead(notificationId);

      setData((previous) =>
        (previous ?? []).map((notification) =>
          notification.id === notificationId
            ? updated
            : notification
        )
      );

      return updated;
    },
    [setData]
  );

  /**
   * ==========================================================
   * MARK ALL AS READ
   * ==========================================================
   */
  const markAllAsRead = useCallback(async () => {
    await notificationService.markAllAsRead();

    setData((previous) =>
      (previous ?? []).map((notification) => ({
        ...notification,
        read: true,
      }))
    );
  }, [setData]);

  /**
   * ==========================================================
   * FILTER BY TYPE
   * ==========================================================
   *
   * Backend returns:
   *
   * NotificationPageResponseDTO
   *
   * so we return response.content to the page.
   */
  const filterByType = useCallback(
    async (type: string): Promise<NotificationResponseDTO[]> => {
      const response =
        await notificationService.getMyNotificationsByType(
          type,
          0,
          50
        );

      return response?.content ?? [];
    },
    []
  );

  return {
    notifications,
    unreadCount,
    loading,
    error,
    reload,
    markAsRead,
    markAllAsRead,
    filterByType,
  };
}