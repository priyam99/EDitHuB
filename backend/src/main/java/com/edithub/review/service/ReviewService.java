package com.edithub.review.service;

import com.edithub.project.model.Project;
import com.edithub.project.model.ProjectVisibility;
import com.edithub.review.dto.CreateReviewRequest;
import com.edithub.review.dto.ReviewDto;
import com.edithub.review.model.Review;
import com.edithub.review.model.ReviewDecision;
import com.edithub.review.repository.ReviewRepository;
import com.edithub.submission.model.Submission;
import com.edithub.submission.model.SubmissionStatus;
import com.edithub.submission.repository.SubmissionRepository;
import com.edithub.user.model.User;
import com.edithub.user.repository.UserRepository;
import com.edithub.version.model.VersionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewDto createReview(UUID submissionId, User reviewer, CreateReviewRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found: " + submissionId));

        if (!submission.getProject().getOwner().getId().equals(reviewer.getId())) {
            throw new IllegalArgumentException("Only the project owner can review submissions");
        }

        Review review = Review.builder()
                .submission(submission)
                .reviewer(reviewer)
                .rating(request.getRating())
                .feedback(request.getFeedback().trim())
                .decision(request.getDecision())
                .build();

        review = reviewRepository.save(review);

        // State Machine Transition
        if (request.getDecision() == ReviewDecision.ACCEPT) {
            submission.setStatus(SubmissionStatus.ACCEPTED);
            submission.getVersion().setStatus(VersionStatus.ACCEPTED);

            // Boost editor reputation
            User editor = submission.getEditor();
            editor.setReputation(editor.getReputation() + 50);
            userRepository.save(editor);
        } else if (request.getDecision() == ReviewDecision.REQUEST_CHANGES) {
            submission.setStatus(SubmissionStatus.CHANGES_REQUESTED);
        } else if (request.getDecision() == ReviewDecision.REJECT) {
            submission.setStatus(SubmissionStatus.REJECTED);
            submission.getVersion().setStatus(VersionStatus.REJECTED);
        }

        submissionRepository.save(submission);
        return ReviewDto.fromEntity(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getSubmissionReviews(UUID submissionId, User currentUser) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        validateProjectAccess(submission.getProject(), currentUser);
        return reviewRepository.findBySubmissionOrderByCreatedAtDesc(submission).stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateProjectAccess(Project project, User currentUser) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            if (currentUser == null || !project.getOwner().getId().equals(currentUser.getId())) {
                throw new SecurityException("Access denied to private project");
            }
        }
    }
}
