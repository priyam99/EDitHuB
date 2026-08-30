package com.edithub.project.dto;

import com.edithub.project.model.Project;
import com.edithub.project.model.ProjectDifficulty;
import com.edithub.project.model.ProjectStatus;
import com.edithub.project.model.ProjectVisibility;
import com.edithub.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    private UUID id;
    private UserDto owner;
    private String title;
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
    private List<ProjectRequirementDto> requirements;
    private Instant createdAt;

    public static ProjectDto fromEntity(Project project) {
        if (project == null) return null;
        return ProjectDto.builder()
                .id(project.getId())
                .owner(UserDto.fromEntity(project.getOwner()))
                .title(project.getTitle())
                .description(project.getDescription())
                .brief(project.getBrief())
                .category(project.getCategory())
                .editingStyle(project.getEditingStyle())
                .targetPlatform(project.getTargetPlatform())
                .aspectRatio(project.getAspectRatio())
                .targetDuration(project.getTargetDuration())
                .deadline(project.getDeadline())
                .difficulty(project.getDifficulty())
                .visibility(project.getVisibility())
                .status(project.getStatus())
                .license(project.getLicense())
                .requirements(project.getRequirements() != null ?
                        project.getRequirements().stream().map(ProjectRequirementDto::fromEntity).toList() : List.of())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
