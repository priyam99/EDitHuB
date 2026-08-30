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

export interface MediaFileDto {
  id: string;
  projectId: string;
  uploadedBy: UserDto;
  fileName: string;
  storageKey: string;
  fileType: 'VIDEO' | 'AUDIO' | 'IMAGE' | 'DOCUMENT' | 'OTHER';
  mimeType: string;
  fileSize: number;
  duration?: number;
  width?: number;
  height?: number;
  thumbnailUrl?: string;
  status: 'UPLOADING' | 'PROCESSING' | 'READY' | 'FAILED';
  createdAt: string;
}

export interface VersionDto {
  id: string;
  projectId: string;
  editor: UserDto;
  parentVersionId?: string;
  versionNumber: number;
  title: string;
  description: string;
  previewKey?: string;
  previewUrl?: string;
  sourceFileKey?: string;
  softwareUsed?: string;
  changes?: string;
  status: 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'ACCEPTED' | 'REJECTED';
  createdAt: string;
}

export interface SubmissionDto {
  id: string;
  projectId: string;
  version: VersionDto;
  editor: UserDto;
  title: string;
  description: string;
  status: 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'CHANGES_REQUESTED' | 'ACCEPTED' | 'REJECTED' | 'WITHDRAWN' | 'CLOSED';
  createdAt: string;
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
