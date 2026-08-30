'use client';

import React from 'react';
import Link from 'next/link';
import { VersionDto } from '@/lib/types';

interface VersionTreeProps {
  versions: VersionDto[];
}

export function VersionTree({ versions }: VersionTreeProps) {
  if (!versions || versions.length === 0) {
    return (
      <div className="py-12 text-center text-slate-400 text-sm font-mono border border-slate-800 rounded-xl bg-slate-950">
        No version contributions submitted yet. Be the first editor to branch and submit an edit!
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between border-b border-slate-800 pb-3 font-mono text-xs text-slate-400">
        <span>Git-style Version Tree</span>
        <span>{versions.length} Version Branches</span>
      </div>

      <div className="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-3 before:bottom-3 before:w-0.5 before:bg-slate-800">
        {versions.map((ver) => (
          <div key={ver.id} className="relative flex items-start gap-4 group">
            {/* Tree Branch Node Dot */}
            <div className="absolute -left-6 top-1.5 w-5 h-5 rounded-full bg-slate-950 border-2 border-indigo-500 flex items-center justify-center text-[10px] font-mono text-indigo-400 font-bold z-10">
              v{ver.versionNumber}
            </div>

            <div className="flex-1 p-5 bg-slate-950 border border-slate-800 group-hover:border-indigo-500/40 rounded-xl transition-all space-y-3">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                <div className="flex items-center gap-2">
                  <span className="font-bold text-white text-base">{ver.title}</span>
                  <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                    ver.status === 'ACCEPTED'
                      ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                      : 'bg-indigo-500/20 text-indigo-400 border border-indigo-500/30'
                  }`}>
                    {ver.status}
                  </span>
                </div>

                <span className="text-xs text-slate-500 font-mono">
                  {new Date(ver.createdAt).toLocaleString()}
                </span>
              </div>

              <p className="text-slate-300 text-xs leading-relaxed">{ver.description}</p>

              {ver.changes && (
                <div className="p-3 bg-slate-900/80 rounded-lg text-xs font-mono text-slate-400 space-y-1">
                  <div className="text-[10px] uppercase text-indigo-400 font-bold">Changes Summary:</div>
                  <div className="whitespace-pre-wrap">{ver.changes}</div>
                </div>
              )}

              <div className="flex flex-wrap items-center justify-between gap-4 pt-2 border-t border-slate-900 text-xs text-slate-400 font-mono">
                <div className="flex items-center gap-2">
                  <div className="w-5 h-5 rounded-full bg-purple-500/20 text-purple-400 flex items-center justify-center font-bold text-[10px]">
                    {ver.editor?.username ? ver.editor.username[0].toUpperCase() : 'E'}
                  </div>
                  <Link href={`/profile/${ver.editor?.username}`} className="hover:text-indigo-400 transition-colors">
                    @{ver.editor?.username}
                  </Link>
                  {ver.softwareUsed && (
                    <span className="px-2 py-0.5 rounded bg-slate-900 border border-slate-800 text-[10px] text-slate-300">
                      💻 {ver.softwareUsed}
                    </span>
                  )}
                </div>

                {ver.previewUrl && (
                  <a
                    href={ver.previewUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="px-3 py-1 rounded bg-indigo-600 hover:bg-indigo-500 text-white font-sans font-bold text-xs transition-all flex items-center gap-1.5"
                  >
                    <span>▶️</span> Watch Preview Edit
                  </a>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
