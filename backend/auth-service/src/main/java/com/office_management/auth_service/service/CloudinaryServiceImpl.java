package com.office_management.auth_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.office_management.auth_service.enums.UserRoles;
import com.office_management.auth_service.exception.errors.InvalidFileException;
import com.office_management.auth_service.service.interfaces.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {
    private static final String DEFAULT_PROFILE_ROOT =
            "office-management/profile";

    private final Cloudinary cloudinary;

    @Value("${cloudinary.profile-root-folder:office-management/profile}")
    private String profileRootFolder;

    public String uploadProfilePhoto(MultipartFile file, Long userId, UserRoles role) {

        validateImageFile(file);
        validateUserDetails(userId, role);
        String roleFolder = role.name();

        String userFolder = buildUserFolder(userId, roleFolder);

        String publicId = userFolder + "/profile";

        log.info("[CLOUDINARY] Profile upload started | userId={} | role={} | folder={} | filename={} | size={}KB", userId, role, userFolder, file.getOriginalFilename(), file.getSize() / 1024);

        try {

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "overwrite", true,
                            "resource_type", "image",
                            "type", "upload",
                            "transformation",
                            new Transformation<>()
                                    .width(400)
                                    .height(400)
                                    .crop("fill")
                                    .gravity("face")
                                    .quality("auto")
                                    .fetchFormat("auto")));

            Object secureUrlObject = result.get("secure_url");

            if (secureUrlObject == null) {
                throw new InvalidFileException("Cloudinary did not return a secure profile photo URL");
            }
            String secureUrl = secureUrlObject.toString();
            if (secureUrl.isBlank()) {
                throw new InvalidFileException("Cloudinary returned an empty profile photo URL");
            }
            log.info("[CLOUDINARY] Profile upload successful | userId={} | role={} | publicId={}", userId, role, publicId);
            return secureUrl;

        } catch (IOException ex) {

            log.error("[CLOUDINARY] Profile upload failed | userId={} | role={} | error={}", userId, role, ex.getMessage(), ex);
            throw new InvalidFileException("Failed to upload profile photo", ex);

        } catch (InvalidFileException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("[CLOUDINARY] Unexpected upload error | userId={} | role={} | error={}",userId, role, ex.getMessage(), ex);
            throw new InvalidFileException("Profile photo upload failed", ex);
        }
    }

    public void deleteProfilePhoto(Long userId, UserRoles role) {
        validateUserDetails(userId, role);
        String publicId = buildUserFolder(userId, role.name()) + "/profile";
        log.info("[CLOUDINARY] Profile delete started | userId={} | role={} | publicId={}", userId, role, publicId);
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image", "type", "upload"));
            String deleteResult = result.get("result") != null ? result.get("result").toString() : "unknown";

            log.info("[CLOUDINARY] Profile delete completed | userId={} | role={} | result={}", userId, role, deleteResult);

        } catch (IOException ex) {
            log.error("[CLOUDINARY] Profile delete failed | userId={} | role={} | error={}", userId, role, ex.getMessage(), ex);

            throw new InvalidFileException("Failed to delete profile photo", ex);

        } catch (Exception ex) {
            log.error("[CLOUDINARY] Unexpected delete error | userId={} | role={} | error={}", userId, role, ex.getMessage(), ex);
            throw new InvalidFileException("Profile photo deletion failed", ex);
        }
    }

    private String buildUserFolder(Long userId, String roleFolder) {
        String root = profileRootFolder == null || profileRootFolder.isBlank()
                        ? DEFAULT_PROFILE_ROOT
                        : profileRootFolder.trim();
        return (root + "/" + roleFolder + "/" + userId);
    }

    private void validateUserDetails(Long userId, UserRoles role) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Profile image cannot be empty");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidFileException("Profile image cannot exceed 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new InvalidFileException("Profile image content type is missing");
        }

        if (!contentType.equalsIgnoreCase("image/jpeg")
                && !contentType.equalsIgnoreCase("image/png")
                && !contentType.equalsIgnoreCase("image/gif")
                && !contentType.equalsIgnoreCase("image/webp")) {

            throw new InvalidFileException("Supported profile image formats are JPG, PNG, GIF and WEBP");
        }
    }
}