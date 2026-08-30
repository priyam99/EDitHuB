package com.edithub.comment.service;

import com.edithub.comment.dto.CommentDto;
import com.edithub.comment.dto.CreateCommentRequest;
import com.edithub.comment.model.Comment;
import com.edithub.comment.repository.CommentRepository;
import com.edithub.project.model.Project;
import com.edithub.project.repository.ProjectRepository;
import com.edithub.submission.model.Submission;
import com.edithub.submission.repository.SubmissionRepository;
import com.edithub.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProjectRepository projectRepository;
    private final SubmissionRepository submissionRepository;

    @Transactional
    public CommentDto createComment(User author, CreateCommentRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + request.getProjectId()));

        Submission submission = null;
        if (request.getSubmissionId() != null) {
            submission = submissionRepository.findById(request.getSubmissionId()).orElse(null);
        }

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId()).orElse(null);
        }

        Comment comment = Comment.builder()
                .project(project)
                .submission(submission)
                .author(author)
                .parentComment(parentComment)
                .content(request.getContent().trim())
                .timestampSeconds(request.getTimestampSeconds())
                .build();

        comment = commentRepository.save(comment);
        return CommentDto.fromEntity(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getProjectComments(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return commentRepository.findByProjectOrderByCreatedAtAsc(project).stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getSubmissionComments(UUID submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        return commentRepository.findBySubmissionOrderByCreatedAtAsc(submission).stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }
}
