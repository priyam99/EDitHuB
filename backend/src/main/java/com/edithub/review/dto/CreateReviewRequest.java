package com.edithub.review.dto;

import com.edithub.review.model.ReviewDecision;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReviewRequest {

    @NotNull(message = "Review decision is required")
    private ReviewDecision decision;

    @NotBlank(message = "Feedback text is required")
    private String feedback;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;
}
