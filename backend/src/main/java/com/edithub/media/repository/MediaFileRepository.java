package com.edithub.media.repository;

import com.edithub.media.model.MediaFile;
import com.edithub.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {

    List<MediaFile> findByProjectOrderByCreatedAtDesc(Project project);

    Optional<MediaFile> findByStorageKey(String storageKey);
}
