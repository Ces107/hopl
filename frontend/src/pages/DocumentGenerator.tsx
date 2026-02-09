import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import api from '../api/client';
import { DocumentType as DocType } from '../types';
import { FileText, Sparkles } from 'lucide-react';

export default function DocumentGenerator() {
  const navigate = useNavigate();
  const { state } = useLocation();
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

  useEffect(() => {
    api.get('/documents/types').then(r => setTypes(r.data));
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const { data } = await api.post('/documents/generate', form);
      navigate(`/documents/${data.id}`);
    } catch (err: any) {
      setError(err.response?.data?.error || 'Generation failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-3xl px-4 py-10">
      <div className="mb-8 text-center">
        <h1 className="mb-2 text-3xl font-bold text-gray-900 dark:text-white">Generate Document</h1>
        <p className="text-gray-500">AI will create a professional document customized to your business.</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {error && <div className="rounded-lg bg-red-50 p-3 text-sm text-red-700 dark:bg-red-900/20 dark:text-red-400">{error}</div>}

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

        <button type="submit" disabled={loading || !form.documentType || !form.businessName}
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
