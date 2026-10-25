package com.office_management.notification_service.mapper;

import com.office_management.notification_service.dto.NotificationResponseDTO;
import com.office_management.notification_service.dto.NotificationSummaryDTO;
import com.office_management.notification_service.dto.PageResponseDTO;
import com.office_management.notification_service.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationMapper {

    public NotificationResponseDTO toResponseDTO(Notification notification)
    {
        if (notification == null) { return null; }

        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .recipientUserId(notification.getRecipientUserId() )
                .type( notification.getType() )
                .title( notification.getTitle() )
                .message( notification.getMessage() )
                .taskId( notification.getTaskId() )
                .eventId( notification.getEventId() )
                .read( notification.isRead() )
                .createdAt( notification.getCreatedAt() )
                .readAt( notification.getReadAt() )
                .build();
    }

    public NotificationSummaryDTO toSummaryDTO(Notification notification ) {
        if (notification == null) { return null; }
        return NotificationSummaryDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .taskId(notification.getTaskId())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }

    public PageResponseDTO<NotificationResponseDTO> toPageResponseDTO(Page<Notification> notificationPage ) {

        if (notificationPage == null) {
        return PageResponseDTO.<NotificationResponseDTO>builder()
            .content(List.of())
            .page(0)
            .size(0)
            .totalElements(0)
            .totalPages(0)
            .first(true)
            .last(true)
            .empty(true)
            .build();
        }
        List<NotificationResponseDTO> content = notificationPage.getContent().stream().map(this::toResponseDTO) .toList();
        return
            PageResponseDTO.<NotificationResponseDTO>builder()
            .content(content)
            .page(notificationPage.getNumber())
            .size(notificationPage.getSize())
            .totalElements(notificationPage.getTotalElements())
            .totalPages(notificationPage.getTotalPages())
            .first(notificationPage.isFirst())
            .last(notificationPage.isLast())
            .empty(notificationPage.isEmpty())
            .build();
    }
// The PageResponseDTO doesn't have a NotificationResponseDTO so please remake the mapper or the implementation of the PageRequestDTO
}