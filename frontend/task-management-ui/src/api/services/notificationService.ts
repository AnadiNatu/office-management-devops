import  { apiService } from "../http/apiService";
import type { NotificationPageResponseDTO, NotificationResponseDTO} from "../models/notificationModels";

class NotificationService {
  /**
   * Get all notifications for the currently authenticated user.
   */
  async getMyNotifications(
    page = 0,
    size = 50
  ): Promise<NotificationPageResponseDTO> {
    return apiService.get<NotificationPageResponseDTO>(
      `/api/notifications/my?page=${page}&size=${size}`
    );
  }

  /**
   * Get unread notification count.
   */
  // async getUnreadCount(): Promise<number> {
  //   const response = await apiService.get<
  //     number | { count: number } | { unreadCount: number }
  //   >("/api/notifications/my/unread-count");

  //   if (typeof response === "number") {
  //     return response;
  //   }

  //   if (
  //     response &&
  //     typeof response === "object" &&
  //     "count" in response
  //   ) {
  //     return Number(response.count ?? 0);
  //   }

  //   if (
  //     response &&
  //     typeof response === "object" &&
  //     "unreadCount" in response
  //   ) {
  //     return Number(response.unreadCount ?? 0);
  //   }

  //   return 0;
  // }

  
async getUnreadCount(): Promise<number> {
  const response = await apiService.get<
    number | { count?: number; unreadCount?: number }
  >("/api/notifications/my/unread/count");

  if (typeof response === "number") {
    return response;
  }

  return Number(
    response?.unreadCount ?? response?.count ?? 0
  );
}


  /**
   * Mark one notification as read.
   *
   * Backend:
   * PUT /api/notifications/{notificationId}/read
   */
  async markAsRead(
    notificationId: number
  ): Promise<NotificationResponseDTO> {
    return apiService.put<NotificationResponseDTO>(
      `/api/notifications/${notificationId}/read`
    );
  }

  /**
   * Mark all notifications belonging to the current user as read.
   *
   * Backend:
   * PUT /api/notifications/my/read-all
   */
  async markAllAsRead(): Promise<void> {
    await apiService.put<void>(
      "/api/notifications/my/read-all"
    );
  }

  /**
   * Kept for compatibility with existing code.
   *
   * IMPORTANT:
   * The backend NotificationType enum contains event-level values
   * such as TASK_CREATED and TASK_ASSIGNED rather than the UI
   * categories TASK / COMMENT / MESSAGE / SYSTEM.
   *
   * Therefore this method loads the user's notifications and
   * performs category filtering on the frontend.
   */
  async getMyNotificationsByType(
    category: string,
    page = 0,
    size = 50
  ): Promise<NotificationPageResponseDTO> {
    const response =
      await this.getMyNotifications(page, size);

    const normalizedCategory =
      category.trim().toUpperCase();

    const filtered =
      response.content.filter((notification) =>
        this.matchesCategory(
          notification.type,
          normalizedCategory
        )
      );

    return {
      ...response,
      content: filtered,
      totalElements: filtered.length,
      totalPages: filtered.length > 0 ? 1 : 0,
      first: true,
      last: true,
      empty: filtered.length === 0,
    };
  }

  /**
   * Convert backend event types into frontend categories.
   */
  private matchesCategory(
    type: string | null | undefined,
    category: string
  ): boolean {
    if (!type) {
      return false;
    }

    const normalizedType =
      type.trim().toUpperCase();

    switch (category) {
      case "TASK":
        return normalizedType.startsWith("TASK");

      case "COMMENT":
        return normalizedType.startsWith("COMMENT");

      case "MESSAGE":
        return normalizedType.startsWith("MESSAGE");

      case "SYSTEM":
        return (
          normalizedType.startsWith("SYSTEM") ||
          normalizedType === "GENERAL" ||
          normalizedType === "INFO"
        );

      default:
        return false;
    }
  }
}

export const notificationService =
  new NotificationService();

export default notificationService;