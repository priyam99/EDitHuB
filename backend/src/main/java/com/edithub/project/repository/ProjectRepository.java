package com.edithub.project.repository;

import com.edithub.project.model.Project;
import com.edithub.project.model.ProjectStatus;
import com.edithub.project.model.ProjectVisibility;
import com.edithub.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Page<Project> findByVisibilityAndStatus(ProjectVisibility visibility, ProjectStatus status, Pageable pageable);

    Page<Project> findByOwner(User owner, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.visibility = :visibility AND p.status = :status AND " +
           "(:category IS NULL OR LOWER(p.category) = LOWER(:category)) AND " +
           "(:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Project> searchProjects(
            @Param("visibility") ProjectVisibility visibility,
            @Param("status") ProjectStatus status,
            @Param("category") String category,
            @Param("search") String search,
            Pageable pageable
    );
}
