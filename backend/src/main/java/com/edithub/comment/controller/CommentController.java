package com.edithub.comment.controller;

import com.edithub.comment.dto.CommentDto;
import com.edithub.comment.dto.CreateCommentRequest;
import com.edithub.comment.service.CommentService;
import com.edithub.common.ApiResponse;
import com.edithub.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public ApiResponse<CommentDto> createComment(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateCommentRequest request) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        CommentDto comment = commentService.createComment(currentUser, request);
        return ApiResponse.ok(comment, "Comment posted successfully");
    }

    @GetMapping("/projects/{projectId}/comments")
    public ApiResponse<List<CommentDto>> getProjectComments(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User currentUser) {
        List<CommentDto> comments = commentService.getProjectComments(projectId, currentUser);
        return ApiResponse.ok(comments);
    }

    @GetMapping("/submissions/{submissionId}/comments")
    public ApiResponse<List<CommentDto>> getSubmissionComments(
            @PathVariable UUID submissionId,
            @AuthenticationPrincipal User currentUser) {
        List<CommentDto> comments = commentService.getSubmissionComments(submissionId, currentUser);
        return ApiResponse.ok(comments);
    }
}
