package com.office_management.auth_service.service.interfaces;

import com.office_management.auth_service.entity.Users;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2Service {
    public Users handleOAuthUser(OAuth2User oauthUser);
    public String determineProvider(OAuth2User oauthUser);
}
