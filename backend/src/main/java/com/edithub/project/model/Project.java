package com.edithub.project.model;

import com.edithub.common.BaseEntity;
import com.edithub.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String brief;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "editing_style", length = 50)
    private String editingStyle;

    @Column(name = "target_platform", length = 50)
    private String targetPlatform;

    @Column(name = "aspect_ratio", length = 20)
    private String aspectRatio;

    @Column(name = "target_duration", length = 50)
    private String targetDuration;

    @Column
    private Instant deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProjectDifficulty difficulty = ProjectDifficulty.INTERMEDIATE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.OPEN;

    @Column(length = 100)
    @Builder.Default
    private String license = "Portfolio Allowed";

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectRequirement> requirements = new ArrayList<>();
}
