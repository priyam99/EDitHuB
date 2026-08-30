package com.edithub.submission.controller;

import com.edithub.common.ApiResponse;
import com.edithub.submission.dto.CreateSubmissionRequest;
import com.edithub.submission.dto.SubmissionDto;
import com.edithub.submission.service.SubmissionService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/projects/{projectId}/submissions")
    public ApiResponse<SubmissionDto> createSubmission(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateSubmissionRequest request) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        SubmissionDto submission = submissionService.createSubmission(projectId, currentUser, request);
        return ApiResponse.ok(submission, "Edit submitted successfully");
    }

    @GetMapping("/projects/{projectId}/submissions")
    public ApiResponse<Page<SubmissionDto>> getProjectSubmissions(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("createdAt").descending());
        Page<SubmissionDto> submissions = submissionService.getProjectSubmissions(projectId, pageable, currentUser);
        return ApiResponse.ok(submissions);
    }

    @GetMapping("/submissions/{id}")
    public ApiResponse<SubmissionDto> getSubmissionDetails(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        SubmissionDto submission = submissionService.getSubmissionById(id, currentUser);
        return ApiResponse.ok(submission);
    }
}
