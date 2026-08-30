package com.edithub.project.repository;

import com.edithub.project.model.Project;
import com.edithub.project.model.ProjectRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRequirementRepository extends JpaRepository<ProjectRequirement, UUID> {

    void deleteByProject(Project project);
}
