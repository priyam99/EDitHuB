package com.edithub.project.service;

import com.edithub.project.dto.CreateProjectRequest;
import com.edithub.project.dto.ProjectDto;
import com.edithub.project.dto.UpdateProjectRequest;
import com.edithub.project.model.Project;
import com.edithub.project.model.ProjectRequirement;
import com.edithub.project.model.ProjectStatus;
import com.edithub.project.model.ProjectVisibility;
import com.edithub.project.repository.ProjectRepository;
import com.edithub.project.repository.ProjectRequirementRepository;
import com.edithub.user.model.Skill;
import com.edithub.user.model.User;
import com.edithub.user.repository.SkillRepository;
import com.edithub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRequirementRepository projectRequirementRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectDto createProject(User owner, CreateProjectRequest request) {
        Project project = Project.builder()
                .owner(owner)
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .brief(request.getBrief().trim())
                .category(request.getCategory().trim())
                .editingStyle(request.getEditingStyle())
                .targetPlatform(request.getTargetPlatform())
                .aspectRatio(request.getAspectRatio())
                .targetDuration(request.getTargetDuration())
                .deadline(request.getDeadline())
                .difficulty(request.getDifficulty() != null ? request.getDifficulty() : com.edithub.project.model.ProjectDifficulty.INTERMEDIATE)
                .visibility(request.getVisibility() != null ? request.getVisibility() : ProjectVisibility.PUBLIC)
                .status(ProjectStatus.OPEN)
                .license(request.getLicense() != null ? request.getLicense() : "Portfolio Allowed")
                .build();

        project = projectRepository.save(project);

        if (request.getRequiredSkillIds() != null && !request.getRequiredSkillIds().isEmpty()) {
            for (UUID skillId : request.getRequiredSkillIds()) {
                Skill skill = skillRepository.findById(skillId).orElse(null);
                if (skill != null) {
                    ProjectRequirement req = ProjectRequirement.builder()
                            .project(project)
                            .skill(skill)
                            .isRequired(true)
                            .build();
                    project.getRequirements().add(req);
                }
            }
            project = projectRepository.save(project);
        }

        return ProjectDto.fromEntity(project);
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectById(UUID id, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            if (currentUser == null || (!currentUser.getId().equals(project.getOwner().getId()) && !currentUser.getRole().name().equals("ADMIN"))) {
                throw new IllegalArgumentException("Access denied to private project");
            }
        }

        return ProjectDto.fromEntity(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> getExploreProjects(String category, String search, Pageable pageable) {
        Page<Project> projects = projectRepository.searchProjects(
                ProjectVisibility.PUBLIC,
                ProjectStatus.OPEN,
                (category != null && !category.isBlank()) ? category : null,
                (search != null && !search.isBlank()) ? search : null,
                pageable
        );
        return projects.map(ProjectDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> getMyProjects(User user, Pageable pageable) {
        return projectRepository.findByOwner(user, pageable).map(ProjectDto::fromEntity);
    }

    @Transactional
    public ProjectDto updateProject(UUID projectId, User currentUser, UpdateProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Only project owner can update project");
        }

        if (request.getTitle() != null) project.setTitle(request.getTitle().trim());
        if (request.getDescription() != null) project.setDescription(request.getDescription().trim());
        if (request.getBrief() != null) project.setBrief(request.getBrief().trim());
        if (request.getCategory() != null) project.setCategory(request.getCategory().trim());
        if (request.getEditingStyle() != null) project.setEditingStyle(request.getEditingStyle());
        if (request.getTargetPlatform() != null) project.setTargetPlatform(request.getTargetPlatform());
        if (request.getAspectRatio() != null) project.setAspectRatio(request.getAspectRatio());
        if (request.getTargetDuration() != null) project.setTargetDuration(request.getTargetDuration());
        if (request.getDeadline() != null) project.setDeadline(request.getDeadline());
        if (request.getDifficulty() != null) project.setDifficulty(request.getDifficulty());
        if (request.getVisibility() != null) project.setVisibility(request.getVisibility());
        if (request.getStatus() != null) project.setStatus(request.getStatus());
        if (request.getLicense() != null) project.setLicense(request.getLicense());

        if (request.getRequiredSkillIds() != null) {
            projectRequirementRepository.deleteByProject(project);
            project.getRequirements().clear();
            for (UUID skillId : request.getRequiredSkillIds()) {
                Skill skill = skillRepository.findById(skillId).orElse(null);
                if (skill != null) {
                    ProjectRequirement req = ProjectRequirement.builder()
                            .project(project)
                            .skill(skill)
                            .isRequired(true)
                            .build();
                    project.getRequirements().add(req);
                }
            }
        }

        project = projectRepository.save(project);
        return ProjectDto.fromEntity(project);
    }

    @Transactional
    public void archiveProject(UUID projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Only project owner can archive project");
        }

        project.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(project);
    }
}
