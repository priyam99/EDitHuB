package com.edithub.submission.repository;

import com.edithub.project.model.Project;
import com.edithub.submission.model.Submission;
import com.edithub.submission.model.SubmissionStatus;
import com.edithub.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    Page<Submission> findByProject(Project project, Pageable pageable);

    List<Submission> findByProjectAndEditor(Project project, User editor);

    boolean existsByProjectAndEditorAndStatusNotIn(Project project, User editor, List<SubmissionStatus> terminalStatuses);
}
