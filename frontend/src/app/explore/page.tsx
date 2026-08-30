'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { api } from '@/lib/api';
import { ProjectDto, PageResponse } from '@/lib/types';

const CATEGORIES = ['All', 'Travel', 'Gaming', 'Vlog', 'Commercial', 'Music Video', 'Short Film', 'Documentary'];

export default function ExplorePage() {
  const [projects, setProjects] = useState<ProjectDto[]>([]);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadExploreProjects() {
      setLoading(true);
      try {
        const categoryParam = selectedCategory === 'All' ? '' : selectedCategory;
        const res = await api.fetch<PageResponse<ProjectDto>>(
          `/projects/explore?category=${encodeURIComponent(categoryParam)}&search=${encodeURIComponent(searchQuery)}`
        );
        if (res.success && res.data) {
          setProjects(res.data.content || []);
        }
      } catch {
        setProjects([]);
      } finally {
        setLoading(false);
      }
    }

    const timer = setTimeout(loadExploreProjects, 300);
    return () => clearTimeout(timer);
  }, [selectedCategory, searchQuery]);

  return (
    <div className="max-w-7xl mx-auto my-8 w-full px-6 space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <h1 className="text-3xl font-extrabold text-white tracking-tight">Explore Projects</h1>
          <p className="text-slate-400 text-sm">Discover open video projects, download raw footage, and contribute edits</p>
        </div>

        <Link
          href="/projects/create"
          className="self-start md:self-auto px-5 py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-sm transition-all shadow-md hover:shadow-indigo-500/20 flex items-center gap-2"
        >
          <span>➕</span> Create Project
        </Link>
      </div>

      {/* Search and Filters */}
      <div className="space-y-4">
        <div className="relative">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search projects by title, description, or requirements..."
            className="w-full px-4 py-3 bg-slate-900/60 border border-slate-800 rounded-xl text-slate-100 placeholder-slate-500 focus:outline-none focus:border-indigo-500 transition-colors text-sm pl-10"
          />
          <span className="absolute left-3.5 top-3 text-slate-500 text-sm">🔍</span>
        </div>

        {/* Category Tabs */}
        <div className="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none">
          {CATEGORIES.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-4 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
                selectedCategory === cat
                  ? 'bg-indigo-600 text-white shadow-sm'
                  : 'bg-slate-900 border border-slate-800 text-slate-400 hover:text-slate-200'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      {/* Projects Grid */}
      {loading ? (
        <div className="py-20 text-center text-slate-500 font-mono text-sm">
          Loading video projects...
        </div>
      ) : projects.length === 0 ? (
        <div className="py-20 text-center bg-slate-900/40 border border-slate-800 rounded-2xl space-y-3">
          <div className="text-4xl">🎬</div>
          <h3 className="text-lg font-bold text-white">No projects found</h3>
          <p className="text-slate-400 text-sm max-w-sm mx-auto">
            Be the first creator to publish a video project open for editor contributions!
          </p>
          <Link
            href="/projects/create"
            className="inline-block px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-bold rounded-lg transition-all"
          >
            Create Project
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {projects.map((project) => (
            <Link
              key={project.id}
              href={`/projects/${project.id}`}
              className="group p-6 bg-slate-900/60 border border-slate-800 hover:border-indigo-500/50 rounded-2xl shadow-lg transition-all flex flex-col justify-between space-y-4"
            >
              <div className="space-y-3">
                <div className="flex items-center justify-between gap-2">
                  <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-purple-500/20 text-purple-400 border border-purple-500/30 uppercase tracking-wider">
                    {project.category}
                  </span>
                  <span className="text-xs text-slate-500 font-mono">
                    {project.aspectRatio || '9:16'} &bull; {project.targetDuration || '30-60s'}
                  </span>
                </div>

                <h3 className="text-xl font-bold text-white group-hover:text-indigo-400 transition-colors line-clamp-1">
                  {project.title}
                </h3>

                <p className="text-slate-400 text-xs line-clamp-2 leading-relaxed">
                  {project.description}
                </p>

                {/* Skill Requirements */}
                {project.requirements && project.requirements.length > 0 && (
                  <div className="flex flex-wrap gap-1.5 pt-1">
                    {project.requirements.map((req) => (
                      <span
                        key={req.id}
                        className="px-2 py-0.5 rounded bg-slate-950 border border-slate-800 text-slate-300 text-[10px]"
                      >
                        {req.skill?.name || 'Skill'}
                      </span>
                    ))}
                  </div>
                )}
              </div>

              <div className="pt-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-slate-400 font-mono">
                <div className="flex items-center gap-2">
                  <div className="w-5 h-5 rounded-full bg-indigo-500/20 text-indigo-400 flex items-center justify-center font-bold text-[10px]">
                    {project.owner?.username ? project.owner.username[0].toUpperCase() : 'C'}
                  </div>
                  <span className="hover:text-slate-200">@{project.owner?.username}</span>
                </div>
                <span className="text-emerald-400 font-medium">Open for Edits</span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
