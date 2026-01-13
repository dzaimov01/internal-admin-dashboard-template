'use client';

import { createContext, ReactNode, useContext, useEffect, useMemo, useState } from 'react';
import { apiClient } from '@/lib/api';

export type AuthUser = {
  email: string;
  fullName: string;
  roles: string[];
};

type AuthContextValue = {
  user: AuthUser | null;
  accessToken: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  hasRole: (role: string) => boolean;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [user, setUser] = useState<AuthUser | null>(null);

  useEffect(() => {
    const storedToken = window.localStorage.getItem('accessToken');
    if (storedToken) {
      setAccessToken(storedToken);
      apiClient.setToken(storedToken);
      apiClient.get<AuthUser>('/api/auth/me').then(setUser).catch(() => {
        setAccessToken(null);
        setUser(null);
        window.localStorage.removeItem('accessToken');
      });
    }
  }, []);

  const login = async (email: string, password: string) => {
    const response = await apiClient.post<{ accessToken: string }>(
      '/api/auth/login',
      { email, password }
    );
    apiClient.setToken(response.accessToken);
    setAccessToken(response.accessToken);
    window.localStorage.setItem('accessToken', response.accessToken);
    const profile = await apiClient.get<AuthUser>('/api/auth/me');
    setUser(profile);
  };

  const logout = () => {
    setAccessToken(null);
    setUser(null);
    apiClient.setToken(null);
    window.localStorage.removeItem('accessToken');
  };

  const hasRole = (role: string) => user?.roles.includes(role) ?? false;

  const value = useMemo(
    () => ({ user, accessToken, login, logout, hasRole }),
    [user, accessToken]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
