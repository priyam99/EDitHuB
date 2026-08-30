'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { useAuth } from '@/context/AuthContext';
import { SkillDto, ProjectDto, ProjectDifficulty, ProjectVisibility } from '@/lib/types';

export default function CreateProjectPage() {
  const router = useRouter();
  const { user } = useAuth();
  const [availableSkills, setAvailableSkills] = useState<SkillDto[]>([]);
  const [formData, setFormData] = useState<{
    title: string;
    description: string;
    brief: string;
    category: string;
    editingStyle: string;
    targetPlatform: string;
    aspectRatio: string;
    targetDuration: string;
    difficulty: ProjectDifficulty;
    visibility: ProjectVisibility;
    license: string;
    requiredSkillIds: string[];
  }>({
    title: '',
    description: '',
    brief: '',
    category: 'Travel',
    editingStyle: 'Cinematic',
    targetPlatform: 'Instagram Reels',
    aspectRatio: '9:16',
    targetDuration: '30-60 seconds',
    difficulty: 'INTERMEDIATE',
    visibility: 'PUBLIC',
    license: 'Portfolio Allowed',
    requiredSkillIds: [],
  });

  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function loadSkills() {
      try {
        const res = await api.fetch<SkillDto[]>('/skills');
        if (res.success && res.data) {
          setAvailableSkills(res.data);
        }
      } catch {
        // Fallback
      }
    }
    loadSkills();
  }, []);

  const toggleSkill = (skillId: string) => {
    setFormData((prev) => ({
      ...prev,
      requiredSkillIds: prev.requiredSkillIds.includes(skillId)
        ? prev.requiredSkillIds.filter((id) => id !== skillId)
        : [...prev.requiredSkillIds, skillId],
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;
    setError(null);
    setLoading(true);

    try {
      const res = await api.fetch<ProjectDto>('/projects', {
        method: 'POST',
        body: JSON.stringify(formData),
      });

      if (res.success && res.data) {
        router.push(`/projects/${res.data.id}`);
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Failed to create project';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="flex-1 flex items-center justify-center p-8 text-slate-400 font-mono">
        Please log in to create a video project.
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto my-12 w-full px-6 space-y-8">
      <div className="border-b border-slate-800 pb-4">
        <h1 className="text-3xl font-extrabold text-white tracking-tight">Create Video Project</h1>
        <p className="text-slate-400 text-sm">Upload footage requirements and open your project for editor contributions</p>
      </div>

      {error && (
        <div className="p-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-400 text-sm font-medium">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6 bg-slate-900/60 border border-slate-800 p-8 rounded-2xl shadow-xl backdrop-blur-md">
        {/* Project Title */}
        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Project Title *
          </label>
          <input
            type="text"
            required
            maxLength={200}
            value={formData.title}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            placeholder="e.g. Goa Travel Reel, Cyberpunk Gaming Montage"
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
          />
        </div>

        {/* Short Description */}
        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Short Summary *
          </label>
          <input
            type="text"
            required
            maxLength={500}
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            placeholder="Turn my raw travel footage into a cinematic fast-paced Instagram Reel."
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
          />
        </div>

        {/* Category & Style */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
              Category *
            </label>
            <select
              value={formData.category}
              onChange={(e) => setFormData({ ...formData, category: e.target.value })}
              className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
            >
              {['Travel', 'Gaming', 'Vlog', 'Commercial', 'Music Video', 'Short Film', 'Documentary'].map((cat) => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
              Target Platform
            </label>
            <input
              type="text"
              value={formData.targetPlatform}
              onChange={(e) => setFormData({ ...formData, targetPlatform: e.target.value })}
              placeholder="Instagram Reels, YouTube Shorts, TikTok"
              className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
            />
          </div>
        </div>

        {/* Technical Specs */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
              Aspect Ratio
            </label>
            <input
              type="text"
              value={formData.aspectRatio}
              onChange={(e) => setFormData({ ...formData, aspectRatio: e.target.value })}
              placeholder="9:16, 16:9, 1:1"
              className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
              Target Duration
            </label>
            <input
              type="text"
              value={formData.targetDuration}
              onChange={(e) => setFormData({ ...formData, targetDuration: e.target.value })}
              placeholder="30-60 seconds"
              className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
              Visibility
            </label>
            <select
              value={formData.visibility}
              onChange={(e) => setFormData({ ...formData, visibility: e.target.value as ProjectVisibility })}
              className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
            >
              <option value="PUBLIC">Public (Open for all)</option>
              <option value="UNLISTED">Unlisted (Link only)</option>
              <option value="PRIVATE">Private (Invited only)</option>
            </select>
          </div>
        </div>

        {/* Project Brief / README */}
        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Editing Brief (Project README) *
          </label>
          <textarea
            required
            rows={6}
            value={formData.brief}
            onChange={(e) => setFormData({ ...formData, brief: e.target.value })}
            placeholder="Describe your editing instructions in detail: pacing, music choices, color grading style, transitions, text overlays..."
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm font-mono"
          />
        </div>

        {/* Skill Requirements */}
        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-2">
            Required Skills
          </label>
          <div className="flex flex-wrap gap-2">
            {availableSkills.map((skill) => {
              const selected = formData.requiredSkillIds.includes(skill.id);
              return (
                <button
                  key={skill.id}
                  type="button"
                  onClick={() => toggleSkill(skill.id)}
                  className={`px-3 py-1 rounded-full text-xs font-semibold border transition-all ${
                    selected
                      ? 'bg-indigo-600 border-indigo-500 text-white'
                      : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-slate-200'
                  }`}
                >
                  {skill.name}
                </button>
              );
            })}
          </div>
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
            disabled={loading}
            className="px-6 py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-sm transition-all shadow-md hover:shadow-indigo-500/20 disabled:opacity-50"
          >
            {loading ? 'Publishing Project...' : 'Publish Project'}
          </button>
        </div>
      </form>
    </div>
  );
}
