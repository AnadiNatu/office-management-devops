package com.office_management.notification_service.controller;


import com.office_management.notification_service.dto.NotificationCountDTO;
import com.office_management.notification_service.dto.NotificationResponseDTO;
import com.office_management.notification_service.dto.PageResponseDTO;
import com.office_management.notification_service.enums.NotificationType;
import com.office_management.notification_service.security.CustomUserDetails;
import com.office_management.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // GET ALL MY NOTIFICATIONS
    @GetMapping("/my")
    public ResponseEntity<PageResponseDTO<NotificationResponseDTO>>
    getMyNotifications(Authentication authentication, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Long currentUserId = getCurrentUserId(authentication);
        return ResponseEntity.ok(notificationService.getMyNotifications(currentUserId, page, size));
    }

    // GET MY UNREAD NOTIFICATIONS
    @GetMapping("/my/unread")
    public ResponseEntity<PageResponseDTO<NotificationResponseDTO>>
    getMyUnreadNotifications(Authentication authentication, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Long currentUserId = getCurrentUserId(authentication);
        return ResponseEntity.ok(notificationService.getMyUnreadNotifications(currentUserId, page, size));
    }

    // GET UNREAD COUNT
    @GetMapping("/my/unread/count")
    public ResponseEntity<NotificationCountDTO> getUnreadCount(Authentication authentication) {

        Long currentUserId = getCurrentUserId(authentication);

        return ResponseEntity.ok(notificationService.getUnreadCount(currentUserId));
    }

    // GET ONE NOTIFICATION
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponseDTO>
    getNotificationById(@PathVariable Long notificationId, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        return ResponseEntity.ok(notificationService.getNotificationById(notificationId, currentUserId));
    }

    // MARK ONE AS READ
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable Long notificationId, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        return ResponseEntity.ok(notificationService.markAsRead(notificationId, currentUserId));
    }

    // MARK ALL AS READ
    @PutMapping("/my/read-all")
    public ResponseEntity<NotificationCountDTO> markAllAsRead(Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        notificationService.markAllAsRead(currentUserId);
        return ResponseEntity.ok(notificationService.getUnreadCount(currentUserId));
    }

    // GET MY NOTIFICATIONS BY TYPE
    @GetMapping("/my/type/{type}")
    public ResponseEntity<PageResponseDTO<NotificationResponseDTO>>
    getMyNotificationsByType(@PathVariable NotificationType type, Authentication authentication, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Long currentUserId = getCurrentUserId(authentication);
//        String normalizeType =  type.trim().toLowerCase();
        return ResponseEntity.ok(notificationService.getMyNotificationsByType(currentUserId, type, page, size));
    }

    // CURRENT USER

    private Long getCurrentUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getUserId();
    }

}