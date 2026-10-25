package com.office_management.email_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String body;
    private boolean html;
}