package com.office_management.auth_service.mapper;

import com.office_management.auth_service.dto.UserResponseDTO;
import com.office_management.auth_service.dto.feign.UserSummaryDTO;
import com.office_management.auth_service.dto.kafka.UserCreatedMessage;
import com.office_management.auth_service.dto.kafka.UserProfileUpdatedMessage;
import com.office_management.auth_service.entity.Users;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserMapper {

    public UserResponseDTO toResponseDTO(Users user) {

        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .fname(user.getFname())
                .lname(user.getLname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .profilePicture(user.getProfilePicture())
                .enabled(user.getEnabled())
                .emailVerified(user.getEmailVerified())
                .build();
    }

    public UserSummaryDTO toSummaryDTO(Users user) {

        if (user == null) {
            return null;
        }

        return UserSummaryDTO.builder()
                .id(user.getId())
                .fname(user.getFname())
                .lname(user.getLname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .profilePicture(user.getProfilePicture())
                .enabled(user.getEnabled())
                .build();
    }

    public UserCreatedMessage toUserCreatedMessage(Users user) {

        if (user == null) {
            return null;
        }

        return UserCreatedMessage.builder()
                .eventId(UUID.randomUUID())
                .eventType("USER_CREATED")
                .source("auth-service")
                .occurredAt(LocalDateTime.now())
                .userId(user.getId())
                .fname(user.getFname())
                .lname(user.getLname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .profilePicture(user.getProfilePicture())
                .build();
    }

    public UserProfileUpdatedMessage toProfileUpdatedMessage(Users user) {

        if (user == null) {
            return null;
        }

        return UserProfileUpdatedMessage.builder()
                .eventId(UUID.randomUUID())
                .eventType("USER_PROFILE_UPDATED")
                .source("auth-service")
                .occurredAt(LocalDateTime.now())
                .userId(user.getId())
                .fname(user.getFname())
                .lname(user.getLname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .build();
    }

}
