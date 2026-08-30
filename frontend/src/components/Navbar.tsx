'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';
import { api } from '@/lib/api';

interface NotificationDto {
  id: string;
  type: string;
  title: string;
  message: string;
  link?: string;
  isRead: boolean;
  createdAt: string;
}

export function Navbar() {
  const { user, logout } = useAuth();
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);
  const [showNotifDropdown, setShowNotifDropdown] = useState(false);

  useEffect(() => {
    async function loadNotifs() {
      if (!user) return;
      try {
        const res = await api.fetch<{ content: NotificationDto[] }>('/notifications');
        if (res.success && res.data) {
          setNotifications(res.data.content || []);
        }
      } catch {
        setNotifications([]);
      }
    }
    loadNotifs();
  }, [user]);

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  const markAllRead = async () => {
    try {
      await api.fetch('/notifications/read-all', { method: 'PATCH' });
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
    } catch {
      // ignore
    }
  };

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
            <div className="flex items-center gap-4 relative">
              {/* Notification Bell */}
              <button
                onClick={() => setShowNotifDropdown(!showNotifDropdown)}
                className="relative p-2 text-slate-400 hover:text-white transition-colors rounded-lg bg-slate-900 border border-slate-800"
                title="Notifications"
              >
                🔔
                {unreadCount > 0 && (
                  <span className="absolute -top-1 -right-1 w-4 h-4 bg-indigo-500 text-white rounded-full text-[10px] font-bold flex items-center justify-center">
                    {unreadCount}
                  </span>
                )}
              </button>

              {/* Notification Dropdown */}
              {showNotifDropdown && (
                <div className="absolute right-0 top-12 w-80 p-4 bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl space-y-3 z-50">
                  <div className="flex items-center justify-between border-b border-slate-800 pb-2">
                    <span className="font-bold text-white text-xs">Notifications</span>
                    {unreadCount > 0 && (
                      <button
                        onClick={markAllRead}
                        className="text-[10px] text-indigo-400 hover:underline font-mono"
                      >
                        Mark all read
                      </button>
                    )}
                  </div>

                  {notifications.length === 0 ? (
                    <div className="py-6 text-center text-xs text-slate-500 font-mono">
                      No notifications yet
                    </div>
                  ) : (
                    <div className="space-y-2 max-h-64 overflow-y-auto pr-1">
                      {notifications.map((n) => (
                        <div
                          key={n.id}
                          className={`p-3 rounded-xl border text-xs space-y-1 ${
                            n.isRead
                              ? 'bg-slate-950/40 border-slate-900 text-slate-400'
                              : 'bg-indigo-500/10 border-indigo-500/30 text-slate-200'
                          }`}
                        >
                          <div className="font-bold text-white text-[11px]">{n.title}</div>
                          <p className="text-[11px] leading-relaxed">{n.message}</p>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}

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
