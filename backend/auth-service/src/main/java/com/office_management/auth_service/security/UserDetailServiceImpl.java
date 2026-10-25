package com.office_management.auth_service.security;

import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.exception.errors.UserNotFoundException;
import com.office_management.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new UserNotFoundException("Username/email cannot be empty");
        }
        Users user = userRepository.findByEmailIgnoreCase(username.trim()).orElseThrow(() -> new UserNotFoundException("User not found"));
        return user;
    }
}