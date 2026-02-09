import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Shield, Search, FileText, AlertTriangle, CheckCircle, Globe, Zap } from 'lucide-react';
import api from '../api/client';

export default function Landing() {
  const [url, setUrl] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleScan = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!url.trim()) return;
    setLoading(true);
    setError('');
    try {
      const { data } = await api.post('/scan', { url: url.trim() });
      navigate(`/scan/${data.id}`, { state: data });
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to scan. Please check the URL and try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {/* Hero */}
      <section className="relative overflow-hidden bg-gradient-to-b from-brand-50 to-white px-4 pb-20 pt-20 dark:from-gray-900 dark:to-gray-950">
        <div className="mx-auto max-w-4xl text-center">
          <div className="mb-6 inline-flex items-center gap-2 rounded-full bg-red-100 px-4 py-1.5 text-sm font-medium text-red-700 dark:bg-red-900/30 dark:text-red-400">
            <AlertTriangle className="h-4 w-4" />
            GDPR fines can reach up to &euro;20,000,000
          </div>
          <h1 className="mb-6 text-5xl font-extrabold tracking-tight text-gray-900 sm:text-6xl dark:text-white">
            Is Your Website <span className="text-brand-600">Legal?</span>
          </h1>
          <p className="mb-10 text-xl text-gray-600 dark:text-gray-400">
            Free compliance scan in 30 seconds. Find out if your website meets GDPR, CCPA, and LGPD requirements &mdash; then fix issues instantly with AI-generated legal documents.
          </p>

          {/* Scan Form */}
          <form onSubmit={handleScan} className="mx-auto flex max-w-2xl flex-col gap-3 sm:flex-row">
            <div className="relative flex-1">
              <Globe className="absolute left-4 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400" />
              <input
                type="text"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
                placeholder="Enter your website URL (e.g., example.com)"
                className="w-full rounded-xl border border-gray-300 py-4 pl-12 pr-4 text-lg shadow-sm focus:border-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-500/20 dark:border-gray-700 dark:bg-gray-900 dark:text-white"
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="flex items-center justify-center gap-2 rounded-xl bg-brand-600 px-8 py-4 text-lg font-semibold text-white shadow-lg transition hover:bg-brand-700 disabled:opacity-50"
            >
              {loading ? (
                <>
                  <div className="h-5 w-5 animate-spin rounded-full border-2 border-white border-t-transparent" />
                  Scanning...
                </>
              ) : (
                <>
                  <Search className="h-5 w-5" />
                  Scan Free
                </>
              )}
            </button>
          </form>
          {error && <p className="mt-4 text-sm text-red-600">{error}</p>}

          <div className="mt-6 flex flex-wrap items-center justify-center gap-3">
            {['GDPR', 'CCPA', 'LGPD', 'PIPEDA', 'UK DPA'].map((reg) => (
              <span key={reg} className="rounded-full bg-white px-3 py-1 text-xs font-medium text-gray-600 shadow-sm ring-1 ring-gray-200 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700">
                {reg}
              </span>
            ))}
          </div>
        </div>
      </section>

      {/* How it Works */}
      <section className="bg-white px-4 py-20 dark:bg-gray-950">
        <div className="mx-auto max-w-6xl">
          <h2 className="mb-12 text-center text-3xl font-bold text-gray-900 dark:text-white">
            How It Works
          </h2>
          <div className="grid gap-8 md:grid-cols-3">
            {[
              { icon: Search, title: 'Scan', desc: 'Enter your website URL and get a free compliance audit in under 30 seconds.' },
              { icon: AlertTriangle, title: 'Detect', desc: 'See your compliance score and exactly which legal requirements you\'re missing.' },
              { icon: FileText, title: 'Fix', desc: 'Generate AI-powered legal documents customized to your business and jurisdiction.' },
            ].map(({ icon: Icon, title, desc }) => (
              <div key={title} className="rounded-2xl border border-gray-200 p-8 text-center transition hover:shadow-lg dark:border-gray-800">
                <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-xl bg-brand-100 dark:bg-brand-900/30">
                  <Icon className="h-7 w-7 text-brand-600" />
                </div>
                <h3 className="mb-2 text-xl font-semibold text-gray-900 dark:text-white">{title}</h3>
                <p className="text-gray-600 dark:text-gray-400">{desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="bg-gray-50 px-4 py-20 dark:bg-gray-900">
        <div className="mx-auto max-w-6xl">
          <h2 className="mb-12 text-center text-3xl font-bold text-gray-900 dark:text-white">
            Why Choose HOPL?
          </h2>
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {[
              { icon: Zap, title: 'AI-Powered', desc: 'Documents generated by AI, customized to your specific business and jurisdiction.' },
              { icon: Shield, title: 'Multi-Regulation', desc: 'Covers GDPR, CCPA, LGPD, PIPEDA, UK DPA and more - auto-detected.' },
              { icon: Globe, title: 'Multi-Language', desc: 'Generate documents in English, Spanish, French, German, Portuguese and more.' },
              { icon: FileText, title: '15+ Document Types', desc: 'Privacy policies, terms, NDAs, contracts, proposals, and business documents.' },
              { icon: CheckCircle, title: 'One-Time Purchase', desc: 'No subscriptions required. Pay once, download forever. From just \u20AC4.99.' },
              { icon: Search, title: 'Free Scan', desc: 'Unlimited free compliance scans. No credit card or registration needed.' },
            ].map(({ icon: Icon, title, desc }) => (
              <div key={title} className="flex gap-4 rounded-xl bg-white p-6 shadow-sm dark:bg-gray-800">
                <Icon className="h-6 w-6 shrink-0 text-brand-600" />
                <div>
                  <h3 className="mb-1 font-semibold text-gray-900 dark:text-white">{title}</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Pricing Preview */}
      <section className="bg-white px-4 py-20 dark:bg-gray-950">
        <div className="mx-auto max-w-4xl text-center">
          <h2 className="mb-4 text-3xl font-bold text-gray-900 dark:text-white">Simple, Transparent Pricing</h2>
          <p className="mb-12 text-gray-600 dark:text-gray-400">No subscriptions required. Pay only for what you need.</p>
          <div className="grid gap-6 sm:grid-cols-3">
            <div className="rounded-2xl border border-gray-200 p-8 dark:border-gray-800">
              <div className="mb-2 text-sm font-medium text-gray-500">Quick Fix</div>
              <div className="mb-4 text-4xl font-bold text-gray-900 dark:text-white">&euro;4.99</div>
              <p className="text-sm text-gray-600 dark:text-gray-400">1 AI-generated document</p>
            </div>
            <div className="relative rounded-2xl border-2 border-brand-600 p-8 shadow-lg">
              <div className="absolute -top-3 left-1/2 -translate-x-1/2 rounded-full bg-brand-600 px-3 py-0.5 text-xs font-bold text-white">
                MOST POPULAR
              </div>
              <div className="mb-2 text-sm font-medium text-brand-600">Full Compliance</div>
              <div className="mb-4 text-4xl font-bold text-gray-900 dark:text-white">&euro;29.99</div>
              <p className="text-sm text-gray-600 dark:text-gray-400">All documents for your website</p>
            </div>
            <div className="rounded-2xl border border-gray-200 p-8 dark:border-gray-800">
              <div className="mb-2 text-sm font-medium text-gray-500">Pro</div>
              <div className="mb-4 text-4xl font-bold text-gray-900 dark:text-white">&euro;19.99<span className="text-lg font-normal text-gray-500">/mo</span></div>
              <p className="text-sm text-gray-600 dark:text-gray-400">Unlimited websites &amp; documents</p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-gray-200 bg-gray-50 px-4 py-10 dark:border-gray-800 dark:bg-gray-900">
        <div className="mx-auto max-w-6xl text-center text-sm text-gray-500 dark:text-gray-400">
          <p className="mb-2">&copy; {new Date().getFullYear()} HOPL. All rights reserved.</p>
          <p className="text-xs">Disclaimer: HOPL provides informational content and AI-generated document templates. This is not legal advice. Consult a qualified attorney for legal matters.</p>
        </div>
      </footer>
    </div>
  );
}
