'use client';

import { use, useEffect, useState } from 'react';
import Image from 'next/image';
import { api, UserDto } from '@/lib/api';

export default function ProfilePage({ params }: { params: Promise<{ username: string }> }) {
  const resolvedParams = use(params);
  const username = resolvedParams.username;
  const [profile, setProfile] = useState<UserDto | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadProfile() {
      try {
        const res = await api.fetch<UserDto>(`/users/${username}`);
        if (res.success && res.data) {
          setProfile(res.data);
        }
      } catch (err: unknown) {
        const msg = err instanceof Error ? err.message : 'User profile not found';
        setError(msg);
      } finally {
        setLoading(false);
      }
    }
    loadProfile();
  }, [username]);

  if (loading) {
    return (
      <div className="flex-1 flex items-center justify-center p-12 text-slate-400 font-mono">
        Loading profile...
      </div>
    );
  }

  if (error || !profile) {
    return (
      <div className="flex-1 flex items-center justify-center p-12 text-red-400 font-mono">
        {error || 'User not found'}
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto my-12 w-full px-6 space-y-8">
      {/* Profile Header */}
      <div className="p-8 bg-slate-900/60 border border-slate-800 rounded-2xl shadow-xl backdrop-blur-md flex flex-col md:flex-row items-center md:items-start gap-6">
        <div className="relative w-24 h-24 rounded-full overflow-hidden bg-gradient-to-tr from-indigo-500 via-purple-500 to-pink-500 flex items-center justify-center text-3xl font-extrabold text-white shadow-lg uppercase">
          {profile.avatarUrl ? (
            <Image
              src={profile.avatarUrl}
              alt={profile.username}
              fill
              className="object-cover"
              unoptimized
            />
          ) : (
            profile.displayName ? profile.displayName[0] : profile.username[0]
          )}
        </div>

        <div className="flex-1 text-center md:text-left space-y-2">
          <div className="flex flex-col md:flex-row md:items-center gap-3">
            <h1 className="text-3xl font-extrabold text-white">{profile.displayName}</h1>
            <span className="text-slate-400 font-mono text-sm">@{profile.username}</span>
            <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-indigo-500/20 text-indigo-400 border border-indigo-500/30">
              {profile.role}
            </span>
          </div>

          <p className="text-slate-300 text-sm max-w-2xl">
            {profile.bio || 'No bio provided yet.'}
          </p>

          <div className="pt-2 flex flex-wrap items-center justify-center md:justify-start gap-6 text-xs text-slate-400 font-mono">
            <div>
              Reputation: <span className="text-emerald-400 font-bold">{profile.reputation}</span>
            </div>
            <div>
              Joined: <span className="text-slate-200">{new Date(profile.createdAt).toLocaleDateString()}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Portfolio / Contribution Placeholder */}
      <div className="p-8 bg-slate-900/40 border border-slate-800 rounded-2xl space-y-4">
        <h2 className="text-xl font-bold text-white border-b border-slate-800 pb-2">
          Accepted Contributions & Portfolio
        </h2>
        <p className="text-slate-400 text-sm">
          No accepted video contributions yet. When {profile.displayName || profile.username}&apos;s submissions are accepted by project creators, they will automatically appear here.
        </p>
      </div>
    </div>
  );
}
