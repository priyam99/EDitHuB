'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { api } from '@/lib/api';
import { useAuth } from '@/context/AuthContext';
import { ProjectDto, PageResponse } from '@/lib/types';

export default function DashboardPage() {
  const { user } = useAuth();
  const [myProjects, setMyProjects] = useState<ProjectDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDashboardData() {
      if (!user) return;
      try {
        const res = await api.fetch<PageResponse<ProjectDto>>('/projects/my');
        if (res.success && res.data) {
          setMyProjects(res.data.content || []);
        }
      } catch {
        setMyProjects([]);
      } finally {
        setLoading(false);
      }
    }
    loadDashboardData();
  }, [user]);

  if (!user) {
    return (
      <div className="flex-1 flex items-center justify-center p-12 text-slate-400 font-mono">
        Please log in to access your dashboard.
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto my-8 w-full px-6 space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <h1 className="text-3xl font-extrabold text-white tracking-tight">Dashboard</h1>
          <p className="text-slate-400 text-sm">Welcome back, {user.displayName || user.username}!</p>
        </div>

        <Link
          href="/projects/create"
          className="px-5 py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-sm transition-all shadow-md hover:shadow-indigo-500/20 flex items-center gap-2"
        >
          <span>➕</span> New Video Project
        </Link>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="p-6 bg-slate-900/60 border border-slate-800 rounded-2xl">
          <div className="text-slate-400 text-xs font-mono uppercase">My Projects</div>
          <div className="text-3xl font-extrabold text-white mt-2">{myProjects.length}</div>
        </div>

        <div className="p-6 bg-slate-900/60 border border-slate-800 rounded-2xl">
          <div className="text-slate-400 text-xs font-mono uppercase">Submissions Received</div>
          <div className="text-3xl font-extrabold text-indigo-400 mt-2">0</div>
        </div>

        <div className="p-6 bg-slate-900/60 border border-slate-800 rounded-2xl">
          <div className="text-slate-400 text-xs font-mono uppercase">Accepted Contributions</div>
          <div className="text-3xl font-extrabold text-emerald-400 mt-2">0</div>
        </div>

        <div className="p-6 bg-slate-900/60 border border-slate-800 rounded-2xl">
          <div className="text-slate-400 text-xs font-mono uppercase">Reputation Score</div>
          <div className="text-3xl font-extrabold text-purple-400 mt-2">{user.reputation}</div>
        </div>
      </div>

      {/* My Projects Section */}
      <div className="space-y-4">
        <h2 className="text-xl font-bold text-white flex items-center gap-2">
          <span>🎬</span> My Video Projects
        </h2>

        {loading ? (
          <div className="py-12 text-center text-slate-500 font-mono text-sm">
            Loading your projects...
          </div>
        ) : myProjects.length === 0 ? (
          <div className="py-12 text-center bg-slate-900/40 border border-slate-800 rounded-2xl space-y-3">
            <p className="text-slate-400 text-sm">You haven&apos;t created any video projects yet.</p>
            <Link
              href="/projects/create"
              className="inline-block px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-bold rounded-lg transition-all"
            >
              Create Your First Project
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {myProjects.map((project) => (
              <Link
                key={project.id}
                href={`/projects/${project.id}`}
                className="group p-6 bg-slate-900/60 border border-slate-800 hover:border-indigo-500/50 rounded-2xl shadow-lg transition-all space-y-3"
              >
                <div className="flex items-center justify-between">
                  <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-indigo-500/20 text-indigo-400 border border-indigo-500/30 uppercase">
                    {project.status}
                  </span>
                  <span className="text-xs text-slate-500 font-mono">{project.visibility}</span>
                </div>

                <h3 className="text-lg font-bold text-white group-hover:text-indigo-400 transition-colors line-clamp-1">
                  {project.title}
                </h3>

                <p className="text-slate-400 text-xs line-clamp-2">
                  {project.description}
                </p>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
