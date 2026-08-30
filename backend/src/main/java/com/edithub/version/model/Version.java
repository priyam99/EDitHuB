package com.edithub.version.model;

import com.edithub.project.model.Project;
import com.edithub.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "versions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editor_id", nullable = false)
    private User editor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_version_id")
    private Version parentVersion;

    @Column(name = "version_number", nullable = false)
    @Builder.Default
    private Integer versionNumber = 1;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "preview_key", length = 500)
    private String previewKey;

    @Column(name = "source_file_key", length = 500)
    private String sourceFileKey;

    @Column(name = "software_used", length = 100)
    private String softwareUsed;

    @Column(columnDefinition = "TEXT")
    private String changes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VersionStatus status = VersionStatus.SUBMITTED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
