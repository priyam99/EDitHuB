package com.edithub.project.dto;

import com.edithub.project.model.ProjectDifficulty;
import com.edithub.project.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class CreateProjectRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Brief is required")
    private String brief;

    @NotBlank(message = "Category is required")
    private String category;

    private String editingStyle;
    private String targetPlatform;
    private String aspectRatio;
    private String targetDuration;
    private Instant deadline;

    private ProjectDifficulty difficulty = ProjectDifficulty.INTERMEDIATE;
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
    private String license = "Portfolio Allowed";

    private List<UUID> requiredSkillIds;
}
