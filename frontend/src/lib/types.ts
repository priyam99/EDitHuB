import { UserDto } from './api';

export type ProjectVisibility = 'PUBLIC' | 'UNLISTED' | 'PRIVATE';
export type ProjectStatus = 'DRAFT' | 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'ARCHIVED';
export type ProjectDifficulty = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';

export interface SkillDto {
  id: string;
  name: string;
  category: string;
}

export interface ProjectRequirementDto {
  id: string;
  skill: SkillDto;
  isRequired: boolean;
}

export interface ProjectDto {
  id: string;
  owner: UserDto;
  title: string;
  description: string;
  brief: string;
  category: string;
  editingStyle?: string;
  targetPlatform?: string;
  aspectRatio?: string;
  targetDuration?: string;
  deadline?: string;
  difficulty: ProjectDifficulty;
  visibility: ProjectVisibility;
  status: ProjectStatus;
  license: string;
  requirements: ProjectRequirementDto[];
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface CreateProjectRequest {
  title: string;
  description: string;
  brief: string;
  category: string;
  editingStyle?: string;
  targetPlatform?: string;
  aspectRatio?: string;
  targetDuration?: string;
  deadline?: string;
  difficulty: ProjectDifficulty;
  visibility: ProjectVisibility;
  license?: string;
  requiredSkillIds?: string[];
}
