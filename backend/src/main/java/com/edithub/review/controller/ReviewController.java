package com.edithub.review.controller;

import com.edithub.common.ApiResponse;
import com.edithub.review.dto.CreateReviewRequest;
import com.edithub.review.dto.ReviewDto;
import com.edithub.review.service.ReviewService;
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
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/submissions/{id}/reviews")
    public ApiResponse<ReviewDto> createReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateReviewRequest request) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        ReviewDto review = reviewService.createReview(id, currentUser, request);
        return ApiResponse.ok(review, "Review submitted successfully");
    }

    @GetMapping("/submissions/{id}/reviews")
    public ApiResponse<List<ReviewDto>> getSubmissionReviews(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        List<ReviewDto> reviews = reviewService.getSubmissionReviews(id, currentUser);
        return ApiResponse.ok(reviews);
    }
}
