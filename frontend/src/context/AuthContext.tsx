'use client';

import React, { createContext, useContext, useEffect, useState } from 'react';
import { api, UserDto, AuthResponse } from '@/lib/api';

interface AuthContextType {
  user: UserDto | null;
  loading: boolean;
  login: (authData: AuthResponse) => void;
  logout: () => Promise<void>;
  updateUser: (user: UserDto) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserDto | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadUser() {
      if (api.getAccessToken()) {
        try {
          const res = await api.fetch<UserDto>('/users/me');
          if (res.success) {
            setUser(res.data);
          }
        } catch {
          api.clearTokens();
          setUser(null);
        }
      }
      setLoading(false);
    }
    loadUser();
  }, []);

  const login = (authData: AuthResponse) => {
    api.setTokens(authData.accessToken, authData.refreshToken);
    setUser(authData.user);
  };

  const logout = async () => {
    try {
      await api.fetch('/auth/logout', { method: 'POST' });
    } catch {
      // Ignore network failure on logout
    } finally {
      api.clearTokens();
      setUser(null);
    }
  };

  const updateUser = (updatedUser: UserDto) => {
    setUser(updatedUser);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, updateUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
