package com.edithub.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateCommentRequest {

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    private UUID submissionId;
    private UUID parentCommentId;

    @NotBlank(message = "Comment content is required")
    private String content;

    private Double timestampSeconds;
}
