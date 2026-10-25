package com.office_management.auth_service.dto.feign;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilePictureSyncDTO {
    private Long userId;
    private String profilePicture;
}