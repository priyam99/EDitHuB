package com.edithub.submission.model;

import com.edithub.common.BaseEntity;
import com.edithub.project.model.Project;
import com.edithub.user.model.User;
import com.edithub.version.model.Version;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private Version version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editor_id", nullable = false)
    private User editor;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;
}
