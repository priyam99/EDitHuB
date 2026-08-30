package com.edithub.config;

import com.edithub.auth.security.JwtAuthenticationFilter;
import com.edithub.auth.security.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitingFilter rateLimitingFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/health", "/api/v1/info").permitAll()
                .requestMatchers("/api/v1/users/{username}").permitAll()
                .requestMatchers("/api/v1/skills").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/projects/explore").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/projects/{id}").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/projects/{projectId}/media").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/media/{id}/download-url").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/projects/{projectId}/versions").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/versions/{id}").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/projects/{projectId}/submissions").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/submissions/{id}").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/submissions/{id}/reviews").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/projects/{projectId}/comments").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/submissions/{submissionId}/comments").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
