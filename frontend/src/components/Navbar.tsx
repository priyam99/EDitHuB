'use client';

import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';

export function Navbar() {
  const { user, logout } = useAuth();

  return (
    <header className="sticky top-0 z-50 bg-slate-950/80 backdrop-blur-md border-b border-slate-800 px-6 py-4">
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        <Link href="/" className="flex items-center gap-2 group">
          <span className="text-2xl transition-transform group-hover:scale-110">🎬</span>
          <span className="text-xl font-bold text-white tracking-tight">EditHub</span>
        </Link>

        <nav className="flex items-center gap-6 text-sm font-medium text-slate-300">
          <Link href="/explore" className="hover:text-white transition-colors">
            Explore
          </Link>
          {user && (
            <Link href="/dashboard" className="hover:text-white transition-colors">
              Dashboard
            </Link>
          )}
        </nav>

        <div className="flex items-center gap-4 text-sm font-medium">
          {user ? (
            <div className="flex items-center gap-4">
              <Link
                href={`/profile/${user.username}`}
                className="flex items-center gap-2 text-slate-200 hover:text-white transition-colors"
              >
                <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-500 flex items-center justify-center font-bold text-white text-xs uppercase">
                  {user.displayName ? user.displayName[0] : user.username[0]}
                </div>
                <span className="hidden sm:inline font-mono text-xs">{user.username}</span>
              </Link>
              <Link
                href="/settings"
                className="text-slate-400 hover:text-slate-200 transition-colors"
                title="Settings"
              >
                ⚙️
              </Link>
              <button
                onClick={() => logout()}
                className="px-3 py-1.5 rounded-lg border border-slate-700 bg-slate-900 text-slate-300 hover:text-white hover:bg-slate-800 transition-all text-xs"
              >
                Logout
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-3">
              <Link
                href="/login"
                className="text-slate-300 hover:text-white transition-colors px-3 py-1.5"
              >
                Log In
              </Link>
              <Link
                href="/register"
                className="px-4 py-1.5 rounded-lg bg-indigo-600 text-white font-semibold hover:bg-indigo-500 transition-all shadow-sm text-xs"
              >
                Sign Up
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
