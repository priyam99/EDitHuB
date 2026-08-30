package com.edithub.media.controller;

import com.edithub.common.ApiResponse;
import com.edithub.media.dto.*;
import com.edithub.media.service.MediaService;
import com.edithub.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/projects/{projectId}/media/upload-url")
    public ApiResponse<UploadUrlResponse> requestUploadUrl(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UploadUrlRequest request) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        UploadUrlResponse response = mediaService.requestUploadUrl(projectId, currentUser, request);
        return ApiResponse.ok(response, "Upload URL generated successfully");
    }

    @PostMapping("/projects/{projectId}/media/complete")
    public ApiResponse<MediaFileDto> completeUpload(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CompleteUploadRequest request) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        MediaFileDto mediaFile = mediaService.completeUpload(projectId, currentUser, request);
        return ApiResponse.ok(mediaFile, "Upload completed successfully");
    }

    @GetMapping("/projects/{projectId}/media")
    public ApiResponse<List<MediaFileDto>> getProjectMedia(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User currentUser) {
        List<MediaFileDto> mediaFiles = mediaService.getProjectMediaFiles(projectId, currentUser);
        return ApiResponse.ok(mediaFiles);
    }

    @GetMapping("/media/{id}/download-url")
    public ApiResponse<Map<String, String>> getDownloadUrl(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        String downloadUrl = mediaService.getDownloadUrl(id, currentUser);
        return ApiResponse.ok(Map.of("downloadUrl", downloadUrl));
    }

    @DeleteMapping("/media/{id}")
    public ApiResponse<Void> deleteMedia(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        mediaService.deleteMediaFile(id, currentUser);
        return ApiResponse.ok(null, "Media file deleted successfully");
    }
}
