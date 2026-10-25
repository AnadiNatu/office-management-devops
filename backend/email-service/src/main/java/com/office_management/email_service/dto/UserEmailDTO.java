package com.office_management.email_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEmailDTO {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private Boolean enabled;
}