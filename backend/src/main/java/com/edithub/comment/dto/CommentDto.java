package com.edithub.comment.dto;

import com.edithub.comment.model.Comment;
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
public class CommentDto {

    private UUID id;
    private UUID projectId;
    private UUID submissionId;
    private UserDto author;
    private UUID parentCommentId;
    private String content;
    private Double timestampSeconds;
    private Instant createdAt;

    public static CommentDto fromEntity(Comment comment) {
        if (comment == null) return null;
        return CommentDto.builder()
                .id(comment.getId())
                .projectId(comment.getProject() != null ? comment.getProject().getId() : null)
                .submissionId(comment.getSubmission() != null ? comment.getSubmission().getId() : null)
                .author(UserDto.fromEntity(comment.getAuthor()))
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .content(comment.getContent())
                .timestampSeconds(comment.getTimestampSeconds())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
