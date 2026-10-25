package com.office_management.auth_service.dto;


import com.office_management.auth_service.enums.UserRoles;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO{
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private String phoneNumber;
    private UserRoles role;
    private String profilePicture;
    private Boolean enabled;
    private Boolean emailVerified;
}