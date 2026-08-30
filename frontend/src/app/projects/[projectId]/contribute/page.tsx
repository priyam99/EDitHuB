'use client';

import { use, useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { useAuth } from '@/context/AuthContext';
import { ProjectDto, VersionDto } from '@/lib/types';
import { FileUploader } from '@/components/upload/FileUploader';

export default function ContributePage({ params }: { params: Promise<{ projectId: string }> }) {
  const resolvedParams = use(params);
  const projectId = resolvedParams.projectId;
  const router = useRouter();
  const { user } = useAuth();
  const [project, setProject] = useState<ProjectDto | null>(null);
  const [versions, setVersions] = useState<VersionDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    parentVersionId: '',
    title: '',
    description: '',
    softwareUsed: 'DaVinci Resolve',
    changes: '',
    previewKey: '',
  });

  useEffect(() => {
    async function loadData() {
      try {
        const [projRes, verRes] = await Promise.all([
          api.fetch<ProjectDto>(`/projects/${projectId}`),
          api.fetch<VersionDto[]>(`/projects/${projectId}/versions`),
        ]);

        if (projRes.success && projRes.data) {
          setProject(projRes.data);
        }
        if (verRes.success && verRes.data) {
          setVersions(verRes.data);
        }
      } catch (err: unknown) {
        const msg = err instanceof Error ? err.message : 'Failed to load project';
        setError(msg);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [projectId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;
    if (!formData.previewKey) {
      setError('Please upload your rendered video edit preview before submitting');
      return;
    }

    setError(null);
    setSubmitting(true);

    try {
      // 1. Create Version Branch
      const versionRes = await api.fetch<VersionDto>(`/projects/${projectId}/versions`, {
        method: 'POST',
        body: JSON.stringify({
          parentVersionId: formData.parentVersionId || undefined,
          title: formData.title,
          description: formData.description,
          previewKey: formData.previewKey,
          softwareUsed: formData.softwareUsed,
          changes: formData.changes,
        }),
      });

      if (!versionRes.success || !versionRes.data) {
        throw new Error(versionRes.message || 'Failed to create version branch');
      }

      // 2. Submit Pull Request / Edit Submission
      const subRes = await api.fetch(`/projects/${projectId}/submissions`, {
        method: 'POST',
        body: JSON.stringify({
          versionId: versionRes.data.id,
          title: formData.title,
          description: formData.description,
        }),
      });

      if (subRes.success) {
        router.push(`/projects/${projectId}`);
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Failed to submit edit';
      setError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex-1 flex items-center justify-center p-12 text-slate-400 font-mono">
        Loading contribution form...
      </div>
    );
  }

  if (!user) {
    return (
      <div className="flex-1 flex items-center justify-center p-12 text-slate-400 font-mono">
        Please log in to contribute an edit.
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto my-12 w-full px-6 space-y-8">
      <div className="border-b border-slate-800 pb-4">
        <h1 className="text-3xl font-extrabold text-white tracking-tight">Contribute Edit (Branch & Submit)</h1>
        <p className="text-slate-400 text-sm">
          Project: <span className="text-white font-bold">{project?.title}</span>
        </p>
      </div>

      {error && (
        <div className="p-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-400 text-sm font-medium">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6 bg-slate-900/60 border border-slate-800 p-8 rounded-2xl shadow-xl backdrop-blur-md">
        {/* Parent Version Branch */}
        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Branch From Version
          </label>
          <select
            value={formData.parentVersionId}
            onChange={(e) => setFormData({ ...formData, parentVersionId: e.target.value })}
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm font-mono"
          >
            <option value="">Root Footage (Version 1 - Original Raw Footage)</option>
            {versions.map((v) => (
              <option key={v.id} value={v.id}>
                v{v.versionNumber}: {v.title} (@{v.editor?.username})
              </option>
            ))}
          </select>
        </div>

        {/* Upload Rendered Video Edit Preview */}
        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-2">
            Upload Rendered Video Edit Preview *
          </label>
          {formData.previewKey ? (
            <div className="p-4 bg-emerald-500/10 border border-emerald-500/30 rounded-xl text-emerald-400 text-sm font-mono flex items-center justify-between">
              <span>✅ Video preview uploaded successfully!</span>
              <button
                type="button"
                onClick={() => setFormData({ ...formData, previewKey: '' })}
                className="text-xs underline hover:text-white"
              >
                Change File
              </button>
            </div>
          ) : (
            <FileUploader
              projectId={projectId}
              onUploadSuccess={(media) => setFormData({ ...formData, previewKey: media.storageKey })}
            />
          )}
        </div>

        {/* Edit Version Title */}
        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Edit Version Title *
          </label>
          <input
            type="text"
            required
            maxLength={200}
            value={formData.title}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            placeholder="e.g. Cinematic Color Grade V1, Fast Paced Cut with SFX"
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
          />
        </div>

        {/* Software Used */}
        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Software Used
          </label>
          <input
            type="text"
            value={formData.softwareUsed}
            onChange={(e) => setFormData({ ...formData, softwareUsed: e.target.value })}
            placeholder="DaVinci Resolve 18, Premiere Pro, After Effects"
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
          />
        </div>

        {/* Edit Description & Changes */}
        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Edit Description & Submission Notes *
          </label>
          <textarea
            required
            rows={4}
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            placeholder="Explain your approach, choices, pacing, and overall style..."
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Summary of Changes (Bullet points)
          </label>
          <textarea
            rows={3}
            value={formData.changes}
            onChange={(e) => setFormData({ ...formData, changes: e.target.value })}
            placeholder="- Applied teal & orange cinematic LUT&#10;- Cut out silent pauses&#10;- Added ambient sound effects"
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm font-mono"
          />
        </div>

        <div className="pt-4 border-t border-slate-800 flex justify-end gap-4">
          <button
            type="button"
            onClick={() => router.back()}
            className="px-5 py-2.5 rounded-lg border border-slate-800 text-slate-400 hover:text-white transition-colors text-sm"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={submitting}
            className="px-6 py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-sm transition-all shadow-md hover:shadow-indigo-500/20 disabled:opacity-50"
          >
            {submitting ? 'Submitting Edit...' : 'Submit Edit Contribution'}
          </button>
        </div>
      </form>
    </div>
  );
}
