package com.office_management.auth_service.dto.kafka;

import com.office_management.auth_service.enums.UserRoles;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedMessage {
    private UUID eventId;
    private String eventType;
    private String source;
    private LocalDateTime occurredAt;

    private Long userId;
    private String fname;
    private String lname;
    private String email;
    private String phoneNumber;
    private UserRoles role;
    private String profilePicture;
}