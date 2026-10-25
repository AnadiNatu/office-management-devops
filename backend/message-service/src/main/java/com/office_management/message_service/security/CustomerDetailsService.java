package com.office_management.message_service.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        throw new UsernameNotFoundException("JWT authentication is used. " + "Users are managed by Auth Service.");
    }
}