'use client';

import React, { useState } from 'react';
import { api } from '@/lib/api';
import { SubmissionDto, ReviewDto } from '@/lib/types';

interface ReviewModalProps {
  submission: SubmissionDto;
  onClose: () => void;
  onReviewSuccess: (updatedSubId: string, decision: string) => void;
}

export function ReviewModal({ submission, onClose, onReviewSuccess }: ReviewModalProps) {
  const [decision, setDecision] = useState<'ACCEPT' | 'REQUEST_CHANGES' | 'REJECT'>('ACCEPT');
  const [rating, setRating] = useState(5);
  const [feedback, setFeedback] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const res = await api.fetch<ReviewDto>(`/submissions/${submission.id}/reviews`, {
        method: 'POST',
        body: JSON.stringify({
          decision,
          rating,
          feedback,
        }),
      });

      if (res.success) {
        onReviewSuccess(submission.id, decision);
        onClose();
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Failed to submit review';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
      <div className="w-full max-w-lg p-6 bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl space-y-6">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <div>
            <h3 className="text-xl font-bold text-white">Review Edit Submission</h3>
            <p className="text-xs text-slate-400 font-mono">Submission: {submission.title}</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-white text-lg">✕</button>
        </div>

        {error && (
          <div className="p-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-400 text-xs font-medium">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Decision selector */}
          <div>
            <label className="block text-xs font-semibold uppercase text-slate-400 mb-2">
              Decision
            </label>
            <div className="grid grid-cols-3 gap-2">
              {[
                { id: 'ACCEPT', label: '✅ Accept', color: 'bg-emerald-600 border-emerald-500 text-white' },
                { id: 'REQUEST_CHANGES', label: '🔄 Request Changes', color: 'bg-amber-600 border-amber-500 text-white' },
                { id: 'REJECT', label: '❌ Reject', color: 'bg-red-600 border-red-500 text-white' },
              ].map((item) => (
                <button
                  key={item.id}
                  type="button"
                  onClick={() => setDecision(item.id as 'ACCEPT' | 'REQUEST_CHANGES' | 'REJECT')}
                  className={`py-2 px-2 text-xs font-bold rounded-lg border transition-all ${
                    decision === item.id
                      ? item.color
                      : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-slate-200'
                  }`}
                >
                  {item.label}
                </button>
              ))}
            </div>
          </div>

          {/* Rating */}
          <div>
            <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
              Rating (1 to 5 Stars)
            </label>
            <div className="flex gap-2">
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  type="button"
                  onClick={() => setRating(star)}
                  className={`text-xl transition-transform ${star <= rating ? 'opacity-100 scale-110' : 'opacity-30'}`}
                >
                  ⭐
                </button>
              ))}
            </div>
          </div>

          {/* Feedback notes */}
          <div>
            <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
              Feedback & Review Notes *
            </label>
            <textarea
              required
              rows={4}
              value={feedback}
              onChange={(e) => setFeedback(e.target.value)}
              placeholder="Provide constructive feedback for the video editor..."
              className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
            />
          </div>

          <div className="pt-3 border-t border-slate-800 flex justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded-lg border border-slate-800 text-slate-400 hover:text-white text-xs font-mono"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-5 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs shadow-md disabled:opacity-50"
            >
              {loading ? 'Submitting...' : 'Submit Decision'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
