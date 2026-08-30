package com.edithub.media.service;

import com.edithub.media.dto.*;
import com.edithub.media.model.MediaFile;
import com.edithub.media.model.MediaFileStatus;
import com.edithub.media.repository.MediaFileRepository;
import com.edithub.project.model.Project;
import com.edithub.project.model.ProjectVisibility;
import com.edithub.project.repository.ProjectRepository;
import com.edithub.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final ProjectRepository projectRepository;
    private final S3StorageService storageService;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "mp4", "mov", "webm", "mkv", "avi",
            "mp3", "wav", "ogg", "flac", "m4a",
            "jpg", "jpeg", "png", "webp", "gif",
            "pdf", "txt"
    );

    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
            "exe", "sh", "bat", "cmd", "com", "msi", "php", "js", "py", "pl",
            "dll", "vbs", "html", "htm", "jsp", "asp", "aspx", "jar", "war", "ps1"
    );

    @Transactional
    public UploadUrlResponse requestUploadUrl(UUID projectId, User currentUser, UploadUrlRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        validateProjectAccess(project, currentUser);

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Only the project owner can upload media files");
        }

        validateFileExtension(request.getFileName());

        String sanitizedFileName = request.getFileName().replaceAll("[^a-zA-Z0-9._-]", "_");
        String storageKey = "projects/" + projectId + "/raw/" + UUID.randomUUID() + "_" + sanitizedFileName;

        MediaFile mediaFile = MediaFile.builder()
                .project(project)
                .uploadedBy(currentUser)
                .fileName(request.getFileName())
                .storageKey(storageKey)
                .fileType(request.getFileType() != null ? request.getFileType() : com.edithub.media.model.MediaFileType.VIDEO)
                .mimeType(request.getMimeType())
                .fileSize(request.getFileSize())
                .checksum(request.getChecksum())
                .status(MediaFileStatus.UPLOADING)
                .build();

        mediaFile = mediaFileRepository.save(mediaFile);

        String uploadUrl = storageService.generatePresignedUploadUrl(storageKey, request.getMimeType(), request.getFileSize());

        return UploadUrlResponse.builder()
                .mediaId(mediaFile.getId())
                .uploadUrl(uploadUrl)
                .storageKey(storageKey)
                .bucket(storageService.getMediaBucket())
                .build();
    }

    @Transactional
    public MediaFileDto completeUpload(UUID projectId, User currentUser, CompleteUploadRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        validateProjectAccess(project, currentUser);

        MediaFile mediaFile = mediaFileRepository.findById(request.getMediaId())
                .orElseThrow(() -> new IllegalArgumentException("Media file not found: " + request.getMediaId()));

        if (!mediaFile.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Media file does not belong to project");
        }

        // Idempotency: if already ready or processing, return existing
        if (mediaFile.getStatus() == MediaFileStatus.READY || mediaFile.getStatus() == MediaFileStatus.PROCESSING) {
            return MediaFileDto.fromEntity(mediaFile);
        }

        if (request.getChecksum() != null) {
            mediaFile.setChecksum(request.getChecksum());
        }

        mediaFile.setStatus(MediaFileStatus.READY);
        mediaFile = mediaFileRepository.save(mediaFile);

        return MediaFileDto.fromEntity(mediaFile);
    }

    @Transactional(readOnly = true)
    public List<MediaFileDto> getProjectMediaFiles(UUID projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        validateProjectAccess(project, currentUser);

        return mediaFileRepository.findByProjectOrderByCreatedAtDesc(project).stream()
                .map(MediaFileDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String getDownloadUrl(UUID mediaId, User currentUser) {
        MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("Media file not found: " + mediaId));

        validateProjectAccess(mediaFile.getProject(), currentUser);

        return storageService.generatePresignedDownloadUrl(mediaFile.getStorageKey());
    }

    @Transactional
    public void deleteMediaFile(UUID mediaId, User currentUser) {
        MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("Media file not found: " + mediaId));

        if (!mediaFile.getProject().getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Only project owner can delete media files");
        }

        mediaFileRepository.delete(mediaFile);
    }

    public void validateProjectAccess(Project project, User currentUser) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            if (currentUser == null || !project.getOwner().getId().equals(currentUser.getId())) {
                throw new SecurityException("Access denied to private project");
            }
        }
    }

    private void validateFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("File name must contain a valid extension");
        }
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if (BLOCKED_EXTENSIONS.contains(ext) || !ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("File extension ." + ext + " is not allowed for uploads");
        }
    }
}
