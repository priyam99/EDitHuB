package com.edithub.version.controller;

import com.edithub.common.ApiResponse;
import com.edithub.user.model.User;
import com.edithub.version.dto.CreateVersionRequest;
import com.edithub.version.dto.VersionDto;
import com.edithub.version.service.VersionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @PostMapping("/projects/{projectId}/versions")
    public ApiResponse<VersionDto> createVersion(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateVersionRequest request) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        VersionDto version = versionService.createVersion(projectId, currentUser, request);
        return ApiResponse.ok(version, "Version created successfully");
    }

    @GetMapping("/projects/{projectId}/versions")
    public ApiResponse<List<VersionDto>> getProjectVersions(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User currentUser) {
        List<VersionDto> versions = versionService.getProjectVersions(projectId, currentUser);
        return ApiResponse.ok(versions);
    }

    @GetMapping("/versions/{id}")
    public ApiResponse<VersionDto> getVersionDetails(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        VersionDto version = versionService.getVersionById(id, currentUser);
        return ApiResponse.ok(version);
    }
}
