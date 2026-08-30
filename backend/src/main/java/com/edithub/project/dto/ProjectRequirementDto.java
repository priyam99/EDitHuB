package com.edithub.project.dto;

import com.edithub.project.model.ProjectRequirement;
import com.edithub.user.dto.SkillDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequirementDto {

    private UUID id;
    private SkillDto skill;
    private Boolean isRequired;

    public static ProjectRequirementDto fromEntity(ProjectRequirement req) {
        if (req == null) return null;
        return ProjectRequirementDto.builder()
                .id(req.getId())
                .skill(SkillDto.fromEntity(req.getSkill()))
                .isRequired(req.getIsRequired())
                .build();
    }
}
