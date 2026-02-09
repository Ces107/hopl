import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import api from '../api/client';
import { UserProfile } from '../types';

interface AuthContextType {
  user: UserProfile | null;
  token: string | null;
  login: (token: string) => void;
  logout: () => void;
  refreshUser: () => Promise<void>;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('hopl_token'));

  const refreshUser = async () => {
    try {
      const { data } = await api.get('/user/me');
      setUser(data);
    } catch {
      setUser(null);
    }
  };

  useEffect(() => {
    if (token) {
      refreshUser();
    }
  }, [token]);

  const login = (newToken: string) => {
    localStorage.setItem('hopl_token', newToken);
    setToken(newToken);
  };

  const logout = () => {
    localStorage.removeItem('hopl_token');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, refreshUser, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
}
