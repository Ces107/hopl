import { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import api from '../api/client';
import { DocumentType as DocType } from '../types';
import { FileText, Sparkles, AlertTriangle, CreditCard } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function DocumentGenerator() {
  const navigate = useNavigate();
  const { state } = useLocation();
  const { isAuthenticated, user, loading: authLoading, refreshUser } = useAuth();
  const [types, setTypes] = useState<DocType[]>([]);
  const [form, setForm] = useState({
    documentType: '',
    businessName: '',
    businessType: '',
    websiteUrl: (state as any)?.url || '',
    jurisdiction: (state as any)?.jurisdiction || 'GLOBAL',
    language: 'English',
    scanId: (state as any)?.scanId || null,
    additionalInfo: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [needsCredits, setNeedsCredits] = useState(false);

  useEffect(() => {
    api.get('/documents/types').then(r => setTypes(r.data));
  }, []);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      navigate('/login', { state: { from: '/documents/generate' }, replace: true });
    }
  }, [authLoading, isAuthenticated, navigate]);

  const hasCredits = user && (user.credits > 0 || user.plan === 'PRO' || user.plan === 'ANNUAL');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setNeedsCredits(false);
    try {
      const { data } = await api.post('/documents/generate', form);
      await refreshUser();
      navigate(`/documents/${data.id}`);
    } catch (err: any) {
      if (err.response?.status === 402) {
        setNeedsCredits(true);
      } else {
        setError(err.response?.data?.error || 'Generation failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  if (authLoading) {
    return (
      <div className="flex h-96 items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-brand-600 border-t-transparent" />
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl px-4 py-10">
      <div className="mb-8 text-center">
        <h1 className="mb-2 text-3xl font-bold text-gray-900 dark:text-white">Generate Document</h1>
        <p className="text-gray-500">AI will create a professional document customized to your business.</p>
        {user && (
          <p className="mt-1 text-sm text-brand-600">
            Credits available: <span className="font-semibold">{user.credits}</span>
          </p>
        )}
      </div>

      {!hasCredits && (
        <div className="mb-6 flex items-start gap-3 rounded-xl border border-amber-200 bg-amber-50 p-4 dark:border-amber-900 dark:bg-amber-900/10">
          <CreditCard className="mt-0.5 h-5 w-5 shrink-0 text-amber-600" />
          <div>
            <p className="font-medium text-amber-800 dark:text-amber-400">No credits available</p>
            <p className="mt-1 text-sm text-amber-700 dark:text-amber-500">
              You need credits to generate documents.{' '}
              <Link to="/pricing" className="font-semibold underline hover:no-underline">Get a plan</Link>
            </p>
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {error && (
          <div className="flex items-start gap-2 rounded-lg bg-red-50 p-3 text-sm text-red-700 dark:bg-red-900/20 dark:text-red-400">
            <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0" />
            {error}
          </div>
        )}

        {needsCredits && (
          <div className="flex items-start gap-3 rounded-xl border border-amber-200 bg-amber-50 p-4 dark:border-amber-900 dark:bg-amber-900/10">
            <CreditCard className="mt-0.5 h-5 w-5 shrink-0 text-amber-600" />
            <div>
              <p className="font-medium text-amber-800 dark:text-amber-400">No credits remaining</p>
              <p className="mt-1 text-sm text-amber-700 dark:text-amber-500">
                Purchase a plan to continue generating documents.{' '}
                <Link to="/pricing" className="font-semibold underline hover:no-underline">View plans</Link>
              </p>
            </div>
          </div>
        )}

        <div>
          <label className="mb-2 block text-sm font-medium text-gray-700 dark:text-gray-300">Document Type</label>
          <div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
            {types.map(t => (
              <button key={t.value} type="button"
                onClick={() => setForm({ ...form, documentType: t.value })}
                className={`flex items-center gap-2 rounded-lg border p-3 text-left text-sm transition ${
                  form.documentType === t.value
                    ? 'border-brand-600 bg-brand-50 text-brand-700 dark:bg-brand-900/20'
                    : 'border-gray-200 text-gray-700 hover:border-gray-300 dark:border-gray-700 dark:text-gray-300'
                }`}>
                <FileText className="h-4 w-4 shrink-0" />
                {t.label}
              </button>
            ))}
          </div>
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-gray-300">Business Name *</label>
            <input type="text" value={form.businessName} onChange={e => setForm({ ...form, businessName: e.target.value })} required
              placeholder="Acme Inc."
              className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-500/20 dark:border-gray-700 dark:bg-gray-900 dark:text-white" />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-gray-300">Business Type</label>
            <input type="text" value={form.businessType} onChange={e => setForm({ ...form, businessType: e.target.value })}
              placeholder="e.g., E-commerce, SaaS, Consulting"
              className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-500/20 dark:border-gray-700 dark:bg-gray-900 dark:text-white" />
          </div>
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-gray-300">Website URL</label>
            <input type="text" value={form.websiteUrl} onChange={e => setForm({ ...form, websiteUrl: e.target.value })}
              placeholder="https://example.com"
              className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-500/20 dark:border-gray-700 dark:bg-gray-900 dark:text-white" />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-gray-300">Jurisdiction</label>
            <select value={form.jurisdiction} onChange={e => setForm({ ...form, jurisdiction: e.target.value })}
              className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-brand-500 focus:outline-none dark:border-gray-700 dark:bg-gray-900 dark:text-white">
              <option value="GLOBAL">Global (Multi-jurisdictional)</option>
              <option value="EU_GDPR">European Union (GDPR)</option>
              <option value="US_CCPA">United States (CCPA)</option>
              <option value="UK_DPA">United Kingdom (UK DPA)</option>
              <option value="BR_LGPD">Brazil (LGPD)</option>
              <option value="CA_PIPEDA">Canada (PIPEDA)</option>
              <option value="AU_PRIVACY">Australia (Privacy Act)</option>
            </select>
          </div>
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-gray-300">Language</label>
          <select value={form.language} onChange={e => setForm({ ...form, language: e.target.value })}
            className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-brand-500 focus:outline-none dark:border-gray-700 dark:bg-gray-900 dark:text-white">
            <option>English</option>
            <option>Spanish</option>
            <option>French</option>
            <option>German</option>
            <option>Portuguese</option>
            <option>Italian</option>
          </select>
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-gray-300">Additional Information</label>
          <textarea value={form.additionalInfo} onChange={e => setForm({ ...form, additionalInfo: e.target.value })}
            rows={3} placeholder="Any specific details about your business, data collected, services offered..."
            className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-500/20 dark:border-gray-700 dark:bg-gray-900 dark:text-white" />
        </div>

        <button type="submit" disabled={loading || !form.documentType || !form.businessName || !hasCredits}
          className="flex w-full items-center justify-center gap-2 rounded-xl bg-brand-600 py-4 text-lg font-semibold text-white transition hover:bg-brand-700 disabled:opacity-50">
          {loading ? (
            <>
              <div className="h-5 w-5 animate-spin rounded-full border-2 border-white border-t-transparent" />
              Generating document...
            </>
          ) : (
            <>
              <Sparkles className="h-5 w-5" />
              Generate Document
            </>
          )}
        </button>
      </form>
    </div>
  );
}
