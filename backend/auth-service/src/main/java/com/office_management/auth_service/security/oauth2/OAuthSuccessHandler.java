package com.office_management.auth_service.security.oauth2;

import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.security.JwtUtil;
import com.office_management.auth_service.service.interfaces.OAuth2Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2Service oauth2Service;

    private final JwtUtil jwtUtil;

    @Value("${app.oauth2.redirect-uri:http://localhost:4200/oauth2/callback}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();

        Users user = oauth2Service.handleOAuthUser(oauthUser);
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        String provider = oauth2Service.determineProvider(oauthUser);

        String redirect = redirectUri + "?token=" + encode(accessToken) + "&refreshToken=" + encode(refreshToken) + "&userId=" +
                        encode(String.valueOf(user.getId())) +
                        "&email=" +
                        encode(user.getEmail()) +
                        "&provider=" +
                        encode(provider);
        response.sendRedirect(redirect);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}