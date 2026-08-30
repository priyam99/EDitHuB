package com.edithub.media.service;

import com.edithub.media.dto.*;
import com.edithub.media.model.MediaFile;
import com.edithub.media.model.MediaFileStatus;
import com.edithub.media.repository.MediaFileRepository;
import com.edithub.project.model.Project;
import com.edithub.project.repository.ProjectRepository;
import com.edithub.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final ProjectRepository projectRepository;
    private final S3StorageService storageService;

    @Transactional
    public UploadUrlResponse requestUploadUrl(UUID projectId, User currentUser, UploadUrlRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Only the project owner can upload media files");
        }

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

        // Mark as ready (or processing when background worker active)
        mediaFile.setStatus(MediaFileStatus.READY);
        mediaFile = mediaFileRepository.save(mediaFile);

        return MediaFileDto.fromEntity(mediaFile);
    }

    @Transactional(readOnly = true)
    public List<MediaFileDto> getProjectMediaFiles(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return mediaFileRepository.findByProjectOrderByCreatedAtDesc(project).stream()
                .map(MediaFileDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String getDownloadUrl(UUID mediaId) {
        MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("Media file not found: " + mediaId));
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
}
