package com.edithub.review.repository;

import com.edithub.review.model.Review;
import com.edithub.submission.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findBySubmissionOrderByCreatedAtDesc(Submission submission);
}
