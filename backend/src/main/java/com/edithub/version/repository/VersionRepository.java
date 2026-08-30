package com.edithub.version.repository;

import com.edithub.project.model.Project;
import com.edithub.user.model.User;
import com.edithub.version.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VersionRepository extends JpaRepository<Version, UUID> {

    List<Version> findByProjectOrderByCreatedAtDesc(Project project);

    List<Version> findByEditor(User editor);
}
