'use client';

import { use, useEffect, useState } from 'react';
import Link from 'next/link';
import { api } from '@/lib/api';
import { useAuth } from '@/context/AuthContext';
import { ProjectDto, MediaFileDto } from '@/lib/types';
import { FileUploader } from '@/components/upload/FileUploader';

type TabType = 'overview' | 'files' | 'versions' | 'submissions' | 'contributors';

export default function ProjectDetailPage({ params }: { params: Promise<{ projectId: string }> }) {
  const resolvedParams = use(params);
  const projectId = resolvedParams.projectId;
  const { user } = useAuth();
  const [project, setProject] = useState<ProjectDto | null>(null);
  const [mediaFiles, setMediaFiles] = useState<MediaFileDto[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<TabType>('overview');

  useEffect(() => {
    async function loadProjectAndMedia() {
      try {
        const [projRes, mediaRes] = await Promise.all([
          api.fetch<ProjectDto>(`/projects/${projectId}`),
          api.fetch<MediaFileDto[]>(`/projects/${projectId}/media`),
        ]);

        if (projRes.success && projRes.data) {
          setProject(projRes.data);
        }
        if (mediaRes.success && mediaRes.data) {
          setMediaFiles(mediaRes.data);
        }
      } catch (err: unknown) {
        const msg = err instanceof Error ? err.message : 'Project not found';
        setError(msg);
      } finally {
        setLoading(false);
      }
    }
    loadProjectAndMedia();
  }, [projectId]);

  const handleDownload = async (mediaId: string) => {
    try {
      const res = await api.fetch<{ downloadUrl: string }>(`/media/${mediaId}/download-url`);
      if (res.success && res.data?.downloadUrl) {
        window.open(res.data.downloadUrl, '_blank');
      }
    } catch {
      alert('Failed to generate download URL');
    }
  };

  const handleUploadSuccess = (newMedia: MediaFileDto) => {
    setMediaFiles((prev) => [newMedia, ...prev]);
  };

  if (loading) {
    return (
      <div className="flex-1 flex items-center justify-center p-12 text-slate-400 font-mono">
        Loading project details...
      </div>
    );
  }

  if (error || !project) {
    return (
      <div className="flex-1 flex items-center justify-center p-12 text-red-400 font-mono">
        {error || 'Project not found'}
      </div>
    );
  }

  const isOwner = user?.id === project.owner?.id;

  return (
    <div className="max-w-7xl mx-auto my-8 w-full px-6 space-y-8">
      {/* Project Header */}
      <div className="p-8 bg-slate-900/60 border border-slate-800 rounded-2xl shadow-xl backdrop-blur-md space-y-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800/80 pb-6">
          <div className="space-y-2">
            <div className="flex items-center gap-3">
              <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-indigo-500/20 text-indigo-400 border border-indigo-500/30 uppercase">
                {project.category}
              </span>
              <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                {project.status}
              </span>
              <span className="text-slate-500 text-xs font-mono">
                {project.visibility}
              </span>
            </div>

            <h1 className="text-3xl font-extrabold text-white tracking-tight">{project.title}</h1>
            <p className="text-slate-300 text-sm max-w-3xl">{project.description}</p>
          </div>

          <div className="flex items-center gap-3 self-start md:self-auto">
            {isOwner && (
              <span className="px-3 py-1 rounded bg-slate-800 text-slate-300 text-xs font-mono">
                Owner
              </span>
            )}
            <Link
              href={`/projects/${project.id}/contribute`}
              className="px-5 py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-sm transition-all shadow-md hover:shadow-indigo-500/20 flex items-center gap-2"
            >
              <span>🌿</span> Contribute Edit
            </Link>
          </div>
        </div>

        {/* Project Metadata Strip */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-xs font-mono text-slate-400 pt-2">
          <div>
            Creator: <Link href={`/profile/${project.owner?.username}`} className="text-indigo-400 hover:underline">@{project.owner?.username}</Link>
          </div>
          <div>
            Aspect Ratio: <span className="text-slate-200">{project.aspectRatio || '9:16'}</span>
          </div>
          <div>
            Duration: <span className="text-slate-200">{project.targetDuration || '30-60s'}</span>
          </div>
          <div>
            License: <span className="text-slate-200">{project.license}</span>
          </div>
        </div>
      </div>

      {/* Tabbed Navigation */}
      <div className="border-b border-slate-800 flex gap-8 font-medium text-sm text-slate-400">
        {[
          { id: 'overview' as TabType, label: '📖 Overview (Brief)' },
          { id: 'files' as TabType, label: `📁 Footage & Assets (${mediaFiles.length})` },
          { id: 'versions' as TabType, label: '🌿 Version Tree' },
          { id: 'submissions' as TabType, label: '🔀 Submissions (PRs)' },
          { id: 'contributors' as TabType, label: '👥 Editors' },
        ].map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`pb-3 border-b-2 transition-colors font-mono ${
              activeTab === tab.id
                ? 'border-indigo-500 text-indigo-400 font-bold'
                : 'border-transparent hover:text-slate-200'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      <div className="bg-slate-900/40 border border-slate-800 rounded-2xl p-8 shadow-md">
        {activeTab === 'overview' && (
          <div className="space-y-6">
            <div className="space-y-3">
              <h2 className="text-xl font-bold text-white flex items-center gap-2">
                <span>📋</span> Editing Requirements & Brief
              </h2>
              <div className="p-6 bg-slate-950 border border-slate-800 rounded-xl font-mono text-sm text-slate-300 leading-relaxed whitespace-pre-wrap">
                {project.brief}
              </div>
            </div>

            {project.requirements && project.requirements.length > 0 && (
              <div className="space-y-2">
                <h3 className="text-sm font-bold uppercase text-slate-400 tracking-wider">Required Skills</h3>
                <div className="flex flex-wrap gap-2">
                  {project.requirements.map((req) => (
                    <span
                      key={req.id}
                      className="px-3 py-1 rounded border border-indigo-500/30 bg-indigo-500/10 text-indigo-300 text-xs font-semibold"
                    >
                      {req.skill?.name || 'Skill'}
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {activeTab === 'files' && (
          <div className="space-y-6">
            {isOwner && (
              <div className="space-y-2">
                <h3 className="text-sm font-bold text-white uppercase font-mono">Upload Raw Footage / Assets</h3>
                <FileUploader projectId={project.id} onUploadSuccess={handleUploadSuccess} />
              </div>
            )}

            <div className="space-y-4">
              <h3 className="text-lg font-bold text-white flex items-center gap-2">
                <span>📁</span> Project Footage ({mediaFiles.length})
              </h3>

              {mediaFiles.length === 0 ? (
                <div className="py-12 text-center text-slate-400 text-sm font-mono border border-slate-800 rounded-xl bg-slate-950">
                  No footage uploaded yet.
                </div>
              ) : (
                <div className="space-y-3">
                  {mediaFiles.map((file) => (
                    <div
                      key={file.id}
                      className="p-4 bg-slate-950 border border-slate-800 rounded-xl flex items-center justify-between gap-4"
                    >
                      <div className="flex items-center gap-3">
                        <span className="text-2xl">
                          {file.fileType === 'AUDIO' ? '🎵' : file.fileType === 'IMAGE' ? '🖼️' : '🎬'}
                        </span>
                        <div>
                          <div className="font-bold text-white text-sm">{file.fileName}</div>
                          <div className="text-xs text-slate-500 font-mono">
                            {(file.fileSize / (1024 * 1024)).toFixed(2)} MB &bull; {file.mimeType} &bull; Status: <span className="text-emerald-400">{file.status}</span>
                          </div>
                        </div>
                      </div>

                      <button
                        onClick={() => handleDownload(file.id)}
                        className="px-4 py-1.5 rounded-lg border border-slate-700 bg-slate-900 hover:bg-slate-800 text-slate-200 hover:text-white font-mono text-xs transition-all flex items-center gap-1.5"
                      >
                        <span>⬇️</span> Download
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        )}

        {activeTab === 'versions' && (
          <div className="py-12 text-center space-y-4">
            <div className="text-4xl">🌿</div>
            <h3 className="text-lg font-bold text-white">Version Control Tree</h3>
            <p className="text-slate-400 text-sm max-w-md mx-auto">
              Visual git-like version tree visualization is coming in Sprint 4.
            </p>
          </div>
        )}

        {activeTab === 'submissions' && (
          <div className="py-12 text-center space-y-4">
            <div className="text-4xl">🔀</div>
            <h3 className="text-lg font-bold text-white">Edit Submissions (Pull Requests)</h3>
            <p className="text-slate-400 text-sm max-w-md mx-auto">
              Editor contributions, reviews, and timeline comments are coming in Sprint 4 & 5.
            </p>
          </div>
        )}

        {activeTab === 'contributors' && (
          <div className="py-12 text-center space-y-4">
            <div className="text-4xl">👥</div>
            <h3 className="text-lg font-bold text-white">Editor Contributors</h3>
            <p className="text-slate-400 text-sm max-w-md mx-auto">
              List of video editors who have contributed edits to this project.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
