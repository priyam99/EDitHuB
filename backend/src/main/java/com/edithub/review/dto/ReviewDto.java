package com.edithub.review.dto;

import com.edithub.review.model.Review;
import com.edithub.review.model.ReviewDecision;
import com.edithub.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private UUID id;
    private UUID submissionId;
    private UserDto reviewer;
    private Integer rating;
    private String feedback;
    private ReviewDecision decision;
    private Instant createdAt;

    public static ReviewDto fromEntity(Review review) {
        if (review == null) return null;
        return ReviewDto.builder()
                .id(review.getId())
                .submissionId(review.getSubmission() != null ? review.getSubmission().getId() : null)
                .reviewer(UserDto.fromEntity(review.getReviewer()))
                .rating(review.getRating())
                .feedback(review.getFeedback())
                .decision(review.getDecision())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
