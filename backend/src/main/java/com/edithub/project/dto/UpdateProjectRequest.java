package com.edithub.project.dto;

import com.edithub.project.model.ProjectDifficulty;
import com.edithub.project.model.ProjectStatus;
import com.edithub.project.model.ProjectVisibility;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateProjectRequest {

    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private String brief;
    private String category;
    private String editingStyle;
    private String targetPlatform;
    private String aspectRatio;
    private String targetDuration;
    private Instant deadline;

    private ProjectDifficulty difficulty;
    private ProjectVisibility visibility;
    private ProjectStatus status;
    private String license;

    private List<UUID> requiredSkillIds;
}
