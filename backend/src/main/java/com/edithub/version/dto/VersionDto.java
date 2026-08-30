package com.edithub.version.dto;

import com.edithub.user.dto.UserDto;
import com.edithub.version.model.Version;
import com.edithub.version.model.VersionStatus;
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
public class VersionDto {

    private UUID id;
    private UUID projectId;
    private UserDto editor;
    private UUID parentVersionId;
    private Integer versionNumber;
    private String title;
    private String description;
    private String previewKey;
    private String previewUrl;
    private String sourceFileKey;
    private String softwareUsed;
    private String changes;
    private VersionStatus status;
    private Instant createdAt;

    public static VersionDto fromEntity(Version version) {
        if (version == null) return null;
        return VersionDto.builder()
                .id(version.getId())
                .projectId(version.getProject() != null ? version.getProject().getId() : null)
                .editor(UserDto.fromEntity(version.getEditor()))
                .parentVersionId(version.getParentVersion() != null ? version.getParentVersion().getId() : null)
                .versionNumber(version.getVersionNumber())
                .title(version.getTitle())
                .description(version.getDescription())
                .previewKey(version.getPreviewKey())
                .sourceFileKey(version.getSourceFileKey())
                .softwareUsed(version.getSoftwareUsed())
                .changes(version.getChanges())
                .status(version.getStatus())
                .createdAt(version.getCreatedAt())
                .build();
    }
}
