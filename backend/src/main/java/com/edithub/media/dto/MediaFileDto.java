package com.edithub.media.dto;

import com.edithub.media.model.MediaFile;
import com.edithub.media.model.MediaFileStatus;
import com.edithub.media.model.MediaFileType;
import com.edithub.user.dto.UserDto;
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
public class MediaFileDto {

    private UUID id;
    private UUID projectId;
    private UserDto uploadedBy;
    private String fileName;
    private String storageKey;
    private MediaFileType fileType;
    private String mimeType;
    private Long fileSize;
    private Double duration;
    private Integer width;
    private Integer height;
    private String thumbnailUrl;
    private MediaFileStatus status;
    private Instant createdAt;

    public static MediaFileDto fromEntity(MediaFile mediaFile) {
        if (mediaFile == null) return null;
        return MediaFileDto.builder()
                .id(mediaFile.getId())
                .projectId(mediaFile.getProject() != null ? mediaFile.getProject().getId() : null)
                .uploadedBy(UserDto.fromEntity(mediaFile.getUploadedBy()))
                .fileName(mediaFile.getFileName())
                .storageKey(mediaFile.getStorageKey())
                .fileType(mediaFile.getFileType())
                .mimeType(mediaFile.getMimeType())
                .fileSize(mediaFile.getFileSize())
                .duration(mediaFile.getDuration())
                .width(mediaFile.getWidth())
                .height(mediaFile.getHeight())
                .status(mediaFile.getStatus())
                .createdAt(mediaFile.getCreatedAt())
                .build();
    }
}
