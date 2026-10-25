package com.office_management.comment_service.dto.feign;


import com.office_management.comment_service.enums.UserRoles;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDTO {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private String phoneNumber;
    private UserRoles role;
    private String profilePicture;
    private Boolean enabled;
}