package com.office_management.auth_service.service;

import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.enums.UserRoles;
import com.office_management.auth_service.exception.errors.ValidationException;
import com.office_management.auth_service.repository.UserRepository;
import com.office_management.auth_service.service.interfaces.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {

    private final UserRepository userRepository;

    public Users handleOAuthUser(
            OAuth2User oauthUser
    ) {

        String email =
                oauthUser.getAttribute("email");

        if (email == null || email.isBlank()) {
            throw new ValidationException(
                    "OAuth2 provider did not provide an email address"
            );
        }

        email = email.trim().toLowerCase();

        String name =
                oauthUser.getAttribute("name");

        if (name == null || name.isBlank()) {

            name =
                    oauthUser.getAttribute("login");
        }

        String firstName = extractFirstName(name);

        String lastName = extractLastName(name);

        String profilePicture =
                extractPicture(oauthUser);

        Users user =
                userRepository
                        .findByEmailIgnoreCase(email)
                        .orElse(null);

        if (user == null) {

            user =
                    Users.builder()
                            .fname(firstName)
                            .lname(lastName)
                            .email(email)
                            .password("")
                            .phoneNumber(null)
                            .role(UserRoles.EMPLOYEE)
                            .profilePicture(profilePicture)
                            .enabled(true)
                            .emailVerified(true)
                            .build();

        } else {

            user.setEmailVerified(true);

            if (profilePicture != null &&
                    !profilePicture.isBlank()) {

                user.setProfilePicture(profilePicture);
            }
        }

        return userRepository.save(user);
    }

    public String determineProvider(
            OAuth2User oauthUser
    ) {

        if (oauthUser.getAttribute("sub") != null) {
            return "GOOGLE";
        }

        if (oauthUser.getAttribute("login") != null) {
            return "GITHUB";
        }

        return "UNKNOWN";
    }

    private String extractPicture(
            OAuth2User oauthUser
    ) {

        String picture =
                oauthUser.getAttribute("picture");

        if (picture != null) {
            return picture;
        }

        return oauthUser.getAttribute("avatar_url");
    }

    private String extractFirstName(String name) {

        if (name == null || name.isBlank()) {
            return "Employee";
        }

        return name.trim()
                .split("\\s+")[0];
    }

    private String extractLastName(String name) {

        if (name == null || name.isBlank()) {
            return "";
        }

        String[] parts =
                name.trim().split("\\s+");

        if (parts.length == 1) {
            return "";
        }

        return String.join(
                " ",
                java.util.Arrays.copyOfRange(
                        parts,
                        1,
                        parts.length
                )
        );
    }

}