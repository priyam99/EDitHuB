package com.edithub.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateSubmissionRequest {

    @NotNull(message = "Version ID is required")
    private UUID versionId;

    @NotBlank(message = "Submission title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;
}
