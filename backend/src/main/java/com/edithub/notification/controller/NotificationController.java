package com.edithub.notification.controller;

import com.edithub.common.ApiResponse;
import com.edithub.notification.dto.NotificationDto;
import com.edithub.notification.service.NotificationService;
import com.edithub.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<Page<NotificationDto>> getNotifications(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<NotificationDto> notifications = notificationService.getUserNotifications(currentUser, pageable);
        return ApiResponse.ok(notifications);
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        notificationService.markAsRead(id, currentUser);
        return ApiResponse.ok(null, "Notification marked as read");
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        notificationService.markAllAsRead(currentUser);
        return ApiResponse.ok(null, "All notifications marked as read");
    }
}
