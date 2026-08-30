package com.edithub.project.controller;

import com.edithub.common.ApiResponse;
import com.edithub.project.dto.CreateProjectRequest;
import com.edithub.project.dto.ProjectDto;
import com.edithub.project.dto.UpdateProjectRequest;
import com.edithub.project.service.ProjectService;
import com.edithub.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ApiResponse<ProjectDto> createProject(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateProjectRequest request) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        ProjectDto project = projectService.createProject(currentUser, request);
        return ApiResponse.ok(project, "Project created successfully");
    }

    @GetMapping("/explore")
    public ApiResponse<Page<ProjectDto>> exploreProjects(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);
        Page<ProjectDto> projects = projectService.getExploreProjects(category, search, pageable);
        return ApiResponse.ok(projects);
    }

    @GetMapping("/my")
    public ApiResponse<Page<ProjectDto>> getMyProjects(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("createdAt").descending());
        Page<ProjectDto> projects = projectService.getMyProjects(currentUser, pageable);
        return ApiResponse.ok(projects);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectDto> getProjectDetails(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        ProjectDto project = projectService.getProjectById(id, currentUser);
        return ApiResponse.ok(project);
    }

    @PatchMapping("/{id}")
    public ApiResponse<ProjectDto> updateProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateProjectRequest request) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        ProjectDto project = projectService.updateProject(id, currentUser, request);
        return ApiResponse.ok(project, "Project updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> archiveProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        projectService.archiveProject(id, currentUser);
        return ApiResponse.ok(null, "Project archived successfully");
    }
}
