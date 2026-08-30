package com.edithub.comment.repository;

import com.edithub.comment.model.Comment;
import com.edithub.project.model.Project;
import com.edithub.submission.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByProjectOrderByCreatedAtAsc(Project project);

    List<Comment> findBySubmissionOrderByCreatedAtAsc(Submission submission);
}
