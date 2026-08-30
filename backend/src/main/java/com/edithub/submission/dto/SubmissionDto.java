package com.edithub.submission.dto;

import com.edithub.submission.model.Submission;
import com.edithub.submission.model.SubmissionStatus;
import com.edithub.user.dto.UserDto;
import com.edithub.version.dto.VersionDto;
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
public class SubmissionDto {

    private UUID id;
    private UUID projectId;
    private VersionDto version;
    private UserDto editor;
    private String title;
    private String description;
    private SubmissionStatus status;
    private Instant createdAt;

    public static SubmissionDto fromEntity(Submission sub) {
        if (sub == null) return null;
        return SubmissionDto.builder()
                .id(sub.getId())
                .projectId(sub.getProject() != null ? sub.getProject().getId() : null)
                .version(VersionDto.fromEntity(sub.getVersion()))
                .editor(UserDto.fromEntity(sub.getEditor()))
                .title(sub.getTitle())
                .description(sub.getDescription())
                .status(sub.getStatus())
                .createdAt(sub.getCreatedAt())
                .build();
    }
}
