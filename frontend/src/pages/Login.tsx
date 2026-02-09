import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import { Shield } from 'lucide-react';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const { data } = await api.post('/auth/login', { email, password });
      login(data.token);
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.error || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-[80vh] items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="mb-8 text-center">
          <Shield className="mx-auto mb-4 h-12 w-12 text-brand-600" />
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Welcome back</h1>
          <p className="text-gray-500">Log in to your HOPL account</p>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          {error && <div className="rounded-lg bg-red-50 p-3 text-sm text-red-700 dark:bg-red-900/20 dark:text-red-400">{error}</div>}
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-gray-300">Email</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} required
              className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-500/20 dark:border-gray-700 dark:bg-gray-900 dark:text-white" />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-gray-300">Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} required
              className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-500/20 dark:border-gray-700 dark:bg-gray-900 dark:text-white" />
          </div>
          <button type="submit" disabled={loading}
            className="w-full rounded-lg bg-brand-600 py-3 font-semibold text-white transition hover:bg-brand-700 disabled:opacity-50">
            {loading ? 'Logging in...' : 'Log in'}
          </button>
        </form>
        <p className="mt-6 text-center text-sm text-gray-500">
          Don't have an account? <Link to="/register" className="font-medium text-brand-600 hover:text-brand-700">Sign up</Link>
        </p>
      </div>
    </div>
  );
}
