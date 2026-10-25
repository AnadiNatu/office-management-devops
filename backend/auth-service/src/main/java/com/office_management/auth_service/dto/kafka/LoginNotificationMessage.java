package com.office_management.auth_service.dto.kafka;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginNotificationMessage {
    private String eventId;

    private String eventType;

    private String source;

    private LocalDateTime occurredAt;

    private Long userId;

    private String email;

    private String fname;

    private String phoneNumber;
}