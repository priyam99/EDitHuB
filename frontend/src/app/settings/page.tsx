'use client';

import { useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import { api, UserDto } from '@/lib/api';

type UserRoleType = 'CREATOR' | 'EDITOR' | 'BOTH';

export default function SettingsPage() {
  const { user, updateUser } = useAuth();
  const [formData, setFormData] = useState<{
    displayName: string;
    bio: string;
    avatarUrl: string;
    role: UserRoleType;
  } | null>(null);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const [loading, setLoading] = useState(false);

  const currentRole = (user?.role === 'ADMIN' ? 'BOTH' : user?.role || 'BOTH') as UserRoleType;
  const activeFormData = formData || {
    displayName: user?.displayName || '',
    bio: user?.bio || '',
    avatarUrl: user?.avatarUrl || '',
    role: currentRole,
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;
    setMessage(null);
    setLoading(true);

    try {
      const res = await api.fetch<UserDto>('/users/me', {
        method: 'PATCH',
        body: JSON.stringify(activeFormData),
      });

      if (res.success && res.data) {
        updateUser(res.data);
        setMessage({ type: 'success', text: 'Profile updated successfully!' });
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Failed to update profile';
      setMessage({ type: 'error', text: msg });
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="flex-1 flex items-center justify-center p-8 text-slate-400">
        Please log in to access settings.
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto my-12 w-full p-6 md:p-8 bg-slate-900/60 border border-slate-800 rounded-2xl shadow-xl space-y-6">
      <div className="border-b border-slate-800 pb-4">
        <h1 className="text-2xl font-bold text-white">Profile Settings</h1>
        <p className="text-slate-400 text-sm">Update your public profile and account preferences</p>
      </div>

      {message && (
        <div
          className={`p-3 rounded-lg border text-sm text-center font-medium ${
            message.type === 'success'
              ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-400'
              : 'bg-red-500/10 border-red-500/30 text-red-400'
          }`}
        >
          {message.text}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Role
          </label>
          <div className="grid grid-cols-3 gap-2">
            {[
              { id: 'CREATOR' as UserRoleType, label: 'Creator' },
              { id: 'EDITOR' as UserRoleType, label: 'Editor' },
              { id: 'BOTH' as UserRoleType, label: 'Both' },
            ].map((item) => (
              <button
                key={item.id}
                type="button"
                onClick={() => setFormData({ ...activeFormData, role: item.id })}
                className={`py-2 px-3 text-xs font-bold rounded-lg border transition-all ${
                  activeFormData.role === item.id
                    ? 'bg-indigo-600 border-indigo-500 text-white'
                    : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-slate-200'
                }`}
              >
                {item.label}
              </button>
            ))}
          </div>
        </div>

        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Display Name
          </label>
          <input
            type="text"
            value={activeFormData.displayName}
            onChange={(e) => setFormData({ ...activeFormData, displayName: e.target.value })}
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Bio
          </label>
          <textarea
            rows={4}
            value={activeFormData.bio}
            onChange={(e) => setFormData({ ...activeFormData, bio: e.target.value })}
            placeholder="Tell creators and editors about yourself, your style, and experience..."
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold uppercase text-slate-400 mb-1">
            Avatar Image URL
          </label>
          <input
            type="url"
            value={activeFormData.avatarUrl}
            onChange={(e) => setFormData({ ...activeFormData, avatarUrl: e.target.value })}
            placeholder="https://example.com/avatar.jpg"
            className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:border-indigo-500 transition-colors text-sm"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="px-6 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-lg transition-all shadow-md text-sm disabled:opacity-50"
        >
          {loading ? 'Saving...' : 'Save Changes'}
        </button>
      </form>
    </div>
  );
}
