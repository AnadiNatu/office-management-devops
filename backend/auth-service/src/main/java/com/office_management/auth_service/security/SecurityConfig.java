package com.office_management.auth_service.security;


import com.office_management.auth_service.security.oauth2.OAuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailServiceImpl userDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final InternalServiceAuthenticationFilter internalServiceAuthenticationFilter;

    private final OAuthSuccessHandler oAuth2SuccessHandler;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC AUTHENTICATION
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/verify-email",
                                "/api/auth/resend-verification").permitAll()

                        // OTP
                        .requestMatchers("/api/otp/**").permitAll()
                        // PASSWORD RESET
                        .requestMatchers("/api/password/forgot", "/api/password/reset" , "/api/auth/password/forgot" , "/api/auth/password/reset").permitAll()
                        // ACTUATOR
                        .requestMatchers("/actuator/health").permitAll()
                        // ADMIN
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        // INTERNAL FEIGN API
                        .requestMatchers("/api/auth/internal/**").hasAuthority("INTERNAL_SERVICE")
                        // MANAGER + ADMIN
                        .requestMatchers("/api/management/**").hasAnyAuthority("ADMIN", "MANAGER")
                        // PROFILE
                        .requestMatchers("/api/profile/**").authenticated()
                        // CURRENT USER
                        .requestMatchers("/api/auth/me").authenticated()
                        // CHANGE PASSWORD
                        .requestMatchers("/api/password/change").authenticated()
                        // OAUTH2
                        .requestMatchers("/oauth2/**", "/login/**", "/api/oauth2/failure").permitAll()

                        // INTERNAL FEIGN APIs
//                        .requestMatchers("/api/auth/internal/**").authenticated()
                        .requestMatchers("/api/auth/internal/**").permitAll()
                        // EVERYTHING ELSE
                        .anyRequest()
                        .authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(internalServiceAuthenticationFilter , UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2SuccessHandler))
                .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}

