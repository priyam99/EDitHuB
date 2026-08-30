package com.edithub.submission.service;

import com.edithub.project.model.Project;
import com.edithub.project.repository.ProjectRepository;
import com.edithub.submission.dto.CreateSubmissionRequest;
import com.edithub.submission.dto.SubmissionDto;
import com.edithub.submission.model.Submission;
import com.edithub.submission.model.SubmissionStatus;
import com.edithub.submission.repository.SubmissionRepository;
import com.edithub.user.model.User;
import com.edithub.version.model.Version;
import com.edithub.version.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ProjectRepository projectRepository;
    private final VersionRepository versionRepository;

    @Transactional
    public SubmissionDto createSubmission(UUID projectId, User editor, CreateSubmissionRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        Version version = versionRepository.findById(request.getVersionId())
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + request.getVersionId()));

        if (!version.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Version does not belong to project");
        }

        // FR-SUB-10: An editor can have at most one open submission per project
        List<SubmissionStatus> terminalStatuses = List.of(SubmissionStatus.ACCEPTED, SubmissionStatus.REJECTED, SubmissionStatus.WITHDRAWN, SubmissionStatus.CLOSED);
        boolean hasOpenSub = submissionRepository.existsByProjectAndEditorAndStatusNotIn(project, editor, terminalStatuses);
        if (hasOpenSub) {
            throw new IllegalArgumentException("You already have an open submission for this project");
        }

        Submission submission = Submission.builder()
                .project(project)
                .version(version)
                .editor(editor)
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .status(SubmissionStatus.SUBMITTED)
                .build();

        submission = submissionRepository.save(submission);
        return SubmissionDto.fromEntity(submission);
    }

    @Transactional(readOnly = true)
    public Page<SubmissionDto> getProjectSubmissions(UUID projectId, Pageable pageable) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return submissionRepository.findByProject(project, pageable).map(SubmissionDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public SubmissionDto getSubmissionById(UUID submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found: " + submissionId));
        return SubmissionDto.fromEntity(submission);
    }
}
