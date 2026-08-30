'use client';

import React, { useEffect, useState } from 'react';
import { api } from '@/lib/api';
import { useAuth } from '@/context/AuthContext';
import { CommentDto } from '@/lib/types';

interface TimelineCommentsProps {
  projectId: string;
  submissionId?: string;
}

export function TimelineComments({ projectId, submissionId }: TimelineCommentsProps) {
  const { user } = useAuth();
  const [comments, setComments] = useState<CommentDto[]>([]);
  const [content, setContent] = useState('');
  const [timestamp, setTimestamp] = useState('');
  const [loading, setLoading] = useState(true);
  const [posting, setPosting] = useState(false);

  useEffect(() => {
    async function loadComments() {
      try {
        const endpoint = submissionId
          ? `/submissions/${submissionId}/comments`
          : `/projects/${projectId}/comments`;
        const res = await api.fetch<CommentDto[]>(endpoint);
        if (res.success && res.data) {
          setComments(res.data);
        }
      } catch {
        setComments([]);
      } finally {
        setLoading(false);
      }
    }
    loadComments();
  }, [projectId, submissionId]);

  const handlePostComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user || !content.trim()) return;

    setPosting(true);
    let seconds: number | undefined = undefined;

    if (timestamp.trim()) {
      // Parse mm:ss into total seconds
      const parts = timestamp.split(':').map((p) => parseFloat(p));
      if (parts.length === 2 && !isNaN(parts[0]) && !isNaN(parts[1])) {
        seconds = parts[0] * 60 + parts[1];
      } else if (parts.length === 1 && !isNaN(parts[0])) {
        seconds = parts[0];
      }
    }

    try {
      const res = await api.fetch<CommentDto>('/comments', {
        method: 'POST',
        body: JSON.stringify({
          projectId,
          submissionId: submissionId || undefined,
          content: content.trim(),
          timestampSeconds: seconds,
        }),
      });

      if (res.success && res.data) {
        setComments((prev) => [...prev, res.data]);
        setContent('');
        setTimestamp('');
      }
    } catch {
      alert('Failed to post comment');
    } finally {
      setPosting(false);
    }
  };

  const formatSeconds = (sec?: number) => {
    if (sec === undefined || sec === null) return null;
    const mins = Math.floor(sec / 60);
    const remainder = Math.floor(sec % 60);
    return `${mins}:${remainder < 10 ? '0' : ''}${remainder}`;
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between border-b border-slate-800 pb-3 font-mono text-xs text-slate-400">
        <span>Timeline Comments ({comments.length})</span>
        <span>Anchored Feedback</span>
      </div>

      {/* Post comment form */}
      {user && (
        <form onSubmit={handlePostComment} className="p-4 bg-slate-950 border border-slate-800 rounded-xl space-y-3">
          <div className="flex items-center gap-3">
            <input
              type="text"
              value={timestamp}
              onChange={(e) => setTimestamp(e.target.value)}
              placeholder="00:45 (Timestamp)"
              className="w-36 px-3 py-1.5 bg-slate-900 border border-slate-800 rounded-lg text-slate-100 font-mono text-xs focus:outline-none focus:border-indigo-500"
            />
            <span className="text-xs text-slate-500 font-mono">Optional video timestamp</span>
          </div>

          <textarea
            required
            rows={2}
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Add a timeline note (e.g., 'Trim 2 seconds off this cut', 'Boost audio volume here')..."
            className="w-full px-3 py-2 bg-slate-900 border border-slate-800 rounded-lg text-slate-100 text-xs focus:outline-none focus:border-indigo-500"
          />

          <div className="flex justify-end">
            <button
              type="submit"
              disabled={posting}
              className="px-4 py-1.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs rounded-lg shadow-sm disabled:opacity-50"
            >
              {posting ? 'Posting...' : 'Post Comment'}
            </button>
          </div>
        </form>
      )}

      {/* Comments List */}
      {loading ? (
        <div className="py-6 text-center text-slate-500 font-mono text-xs">Loading comments...</div>
      ) : comments.length === 0 ? (
        <div className="py-6 text-center text-slate-500 font-mono text-xs bg-slate-950 border border-slate-800 rounded-xl">
          No timeline comments yet.
        </div>
      ) : (
        <div className="space-y-3">
          {comments.map((comment) => (
            <div key={comment.id} className="p-4 bg-slate-950 border border-slate-800 rounded-xl space-y-1">
              <div className="flex items-center justify-between text-xs font-mono">
                <div className="flex items-center gap-2">
                  <span className="font-bold text-white">@{comment.author?.username}</span>
                  {comment.timestampSeconds !== undefined && comment.timestampSeconds !== null && (
                    <span className="px-2 py-0.5 rounded bg-indigo-500/20 text-indigo-400 border border-indigo-500/30 text-[10px] font-bold">
                      ⏱️ {formatSeconds(comment.timestampSeconds)}
                    </span>
                  )}
                </div>
                <span className="text-slate-500">{new Date(comment.createdAt).toLocaleTimeString()}</span>
              </div>
              <p className="text-slate-300 text-xs pt-1">{comment.content}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
