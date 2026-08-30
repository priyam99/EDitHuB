package com.edithub.version.service;

import com.edithub.media.service.S3StorageService;
import com.edithub.project.model.Project;
import com.edithub.project.model.ProjectStatus;
import com.edithub.project.model.ProjectVisibility;
import com.edithub.project.repository.ProjectRepository;
import com.edithub.user.model.User;
import com.edithub.version.dto.CreateVersionRequest;
import com.edithub.version.dto.VersionDto;
import com.edithub.version.model.Version;
import com.edithub.version.model.VersionStatus;
import com.edithub.version.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VersionService {

    private final VersionRepository versionRepository;
    private final ProjectRepository projectRepository;
    private final S3StorageService storageService;

    @Transactional
    public VersionDto createVersion(UUID projectId, User editor, CreateVersionRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        validateProjectAccess(project, editor);

        if (project.getStatus() == ProjectStatus.ARCHIVED || project.getStatus() == ProjectStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot contribute versions to an archived or completed project");
        }

        Version parentVersion = null;
        int versionNum = 1;
        if (request.getParentVersionId() != null) {
            parentVersion = versionRepository.findById(request.getParentVersionId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent version not found"));
            versionNum = parentVersion.getVersionNumber() + 1;
        }

        Version version = Version.builder()
                .project(project)
                .editor(editor)
                .parentVersion(parentVersion)
                .versionNumber(versionNum)
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .previewKey(request.getPreviewKey())
                .sourceFileKey(request.getSourceFileKey())
                .softwareUsed(request.getSoftwareUsed())
                .changes(request.getChanges())
                .status(VersionStatus.SUBMITTED)
                .build();

        version = versionRepository.save(version);
        return attachUrls(VersionDto.fromEntity(version));
    }

    @Transactional(readOnly = true)
    public List<VersionDto> getProjectVersions(UUID projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        validateProjectAccess(project, currentUser);
        return versionRepository.findByProjectOrderByCreatedAtDesc(project).stream()
                .map(VersionDto::fromEntity)
                .map(this::attachUrls)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VersionDto getVersionById(UUID versionId, User currentUser) {
        Version version = versionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));
        validateProjectAccess(version.getProject(), currentUser);
        return attachUrls(VersionDto.fromEntity(version));
    }

    private void validateProjectAccess(Project project, User currentUser) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            if (currentUser == null || !project.getOwner().getId().equals(currentUser.getId())) {
                throw new SecurityException("Access denied to private project");
            }
        }
    }

    private VersionDto attachUrls(VersionDto dto) {
        if (dto != null && dto.getPreviewKey() != null) {
            dto.setPreviewUrl(storageService.generatePresignedDownloadUrl(dto.getPreviewKey()));
        }
        return dto;
    }
}
