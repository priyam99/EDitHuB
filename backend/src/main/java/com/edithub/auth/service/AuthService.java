package com.edithub.auth.service;

import com.edithub.auth.dto.*;
import com.edithub.auth.model.RefreshToken;
import com.edithub.auth.repository.RefreshTokenRepository;
import com.edithub.auth.security.JwtTokenProvider;
import com.edithub.user.dto.UserDto;
import com.edithub.user.model.User;
import com.edithub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = User.builder()
                .username(request.getUsername().toLowerCase().trim())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName() != null ? request.getDisplayName().trim() : request.getUsername().trim())
                .role(request.getRole())
                .isVerified(false)
                .isActive(true)
                .reputation(0)
                .build();

        user = userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole().name());
        String refreshTokenStr = createRefreshToken(user);

        return AuthResponse.of(accessToken, refreshTokenStr, UserDto.fromEntity(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String loginInput = request.getLogin().trim().toLowerCase();

        User user = userRepository.findByEmail(loginInput)
                .or(() -> userRepository.findByUsername(loginInput))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username/email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username/email or password");
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new IllegalArgumentException("Account has been deactivated");
        }

        // Delete old refresh tokens for single-device/clean session or issue new
        refreshTokenRepository.deleteByUser(user);

        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole().name());
        String refreshTokenStr = createRefreshToken(user);

        return AuthResponse.of(accessToken, refreshTokenStr, UserDto.fromEntity(user));
    }

    @Transactional
    public AuthResponse refreshToken(RefreshRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new IllegalArgumentException("Refresh token has expired. Please log in again.");
        }

        User user = token.getUser();
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new IllegalArgumentException("User account is inactive");
        }

        // Single-use refresh token rotation: delete used refresh token
        refreshTokenRepository.delete(token);

        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole().name());
        String newRefreshTokenStr = createRefreshToken(user);

        return AuthResponse.of(newAccessToken, newRefreshTokenStr, UserDto.fromEntity(user));
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
    }

    private String createRefreshToken(User user) {
        String tokenStr = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenStr)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenStr;
    }
}
