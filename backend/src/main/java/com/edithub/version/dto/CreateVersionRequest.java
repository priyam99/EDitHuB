package com.edithub.version.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateVersionRequest {

    private UUID parentVersionId;

    @NotBlank(message = "Version title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Description of edit is required")
    private String description;

    private String previewKey;
    private String sourceFileKey;
    private String softwareUsed;
    private String changes;
}
