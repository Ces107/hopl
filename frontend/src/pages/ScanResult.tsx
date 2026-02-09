import { useLocation, useParams, Link, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { CheckCircle, XCircle, AlertTriangle, FileText, Download } from 'lucide-react';
import api from '../api/client';
import { ScanResponse } from '../types';
import ScoreGauge from '../components/scanner/ScoreGauge';
import { useAuth } from '../context/AuthContext';

export default function ScanResult() {
  const { state } = useLocation();
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [data, setData] = useState<ScanResponse | null>(state as ScanResponse);

  useEffect(() => {
    if (!data && id) {
      api.get(`/scan/${id}`).then(r => setData(r.data)).catch(() => navigate('/'));
    }
  }, [id]);

  if (!data) return <div className="flex h-96 items-center justify-center"><div className="h-8 w-8 animate-spin rounded-full border-4 border-brand-600 border-t-transparent" /></div>;

  const failed = data.issues.filter(i => !i.passed);
  const passed = data.issues.filter(i => i.passed);

  return (
    <div className="mx-auto max-w-5xl px-4 py-10">
      {/* Header */}
      <div className="mb-8 text-center">
        <h1 className="mb-2 text-3xl font-bold text-gray-900 dark:text-white">Compliance Report</h1>
        <p className="text-gray-500 dark:text-gray-400">{data.url}</p>
      </div>

      <div className="grid gap-8 lg:grid-cols-3">
        {/* Score */}
        <div className="flex flex-col items-center rounded-2xl border border-gray-200 p-8 dark:border-gray-800">
          <ScoreGauge score={data.score} />
          <div className="mt-4 text-center">
            <p className="text-sm text-gray-500">Jurisdiction detected:</p>
            <p className="font-medium text-gray-900 dark:text-white">{data.jurisdiction?.replace('_', ' ') || 'Global'}</p>
          </div>
          {data.riskLevel === 'HIGH' && (
            <div className="mt-4 flex items-start gap-2 rounded-lg bg-red-50 p-3 text-sm text-red-700 dark:bg-red-900/20 dark:text-red-400">
              <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0" />
              <span>Your website may be at risk of fines up to &euro;20,000,000 under GDPR.</span>
            </div>
          )}
        </div>

        {/* Issues */}
        <div className="lg:col-span-2">
          {failed.length > 0 && (
            <div className="mb-6">
              <h2 className="mb-3 flex items-center gap-2 text-lg font-semibold text-red-600">
                <XCircle className="h-5 w-5" /> Issues Found ({failed.length})
              </h2>
              <div className="space-y-3">
                {failed.map((issue) => (
                  <div key={issue.code} className="rounded-xl border border-red-200 bg-red-50 p-4 dark:border-red-900 dark:bg-red-900/10">
                    <div className="flex items-start justify-between">
                      <div>
                        <h3 className="font-medium text-gray-900 dark:text-white">{issue.title}</h3>
                        <p className="mt-1 text-sm text-gray-600 dark:text-gray-400">{issue.description}</p>
                      </div>
                      <span className="shrink-0 rounded-full bg-red-100 px-2 py-0.5 text-xs font-medium text-red-700">
                        -{issue.severity}pts
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {passed.length > 0 && (
            <div className="mb-6">
              <h2 className="mb-3 flex items-center gap-2 text-lg font-semibold text-green-600">
                <CheckCircle className="h-5 w-5" /> Passed ({passed.length})
              </h2>
              <div className="space-y-2">
                {passed.map((issue) => (
                  <div key={issue.code} className="flex items-center gap-3 rounded-lg bg-green-50 p-3 dark:bg-green-900/10">
                    <CheckCircle className="h-4 w-4 text-green-500" />
                    <span className="text-sm text-gray-700 dark:text-gray-300">{issue.title}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Recommendations */}
          {data.recommendations && data.recommendations.length > 0 && (
            <div className="mb-6">
              <h2 className="mb-3 text-lg font-semibold text-gray-900 dark:text-white">Recommendations</h2>
              <ul className="space-y-2">
                {data.recommendations.map((rec, i) => (
                  <li key={i} className="flex items-start gap-2 text-sm text-gray-600 dark:text-gray-400">
                    <FileText className="mt-0.5 h-4 w-4 shrink-0 text-brand-600" />
                    {rec}
                  </li>
                ))}
              </ul>
            </div>
          )}

          {/* CTA */}
          {failed.length > 0 && (
            <div className="rounded-2xl bg-gradient-to-r from-brand-600 to-brand-700 p-8 text-center text-white">
              <h3 className="mb-2 text-2xl font-bold">Fix All Issues Now</h3>
              <p className="mb-6 text-brand-100">
                Generate all the missing legal documents for your website, customized by AI.
              </p>
              <div className="flex flex-col items-center gap-3 sm:flex-row sm:justify-center">
                <Link
                  to={isAuthenticated ? '/documents/generate' : '/register'}
                  state={{ scanId: data.id, url: data.url, jurisdiction: data.jurisdiction }}
                  className="inline-flex items-center gap-2 rounded-xl bg-white px-6 py-3 font-semibold text-brand-700 shadow-lg transition hover:bg-brand-50"
                >
                  <Download className="h-5 w-5" />
                  Full Compliance &mdash; &euro;29.99
                </Link>
                <Link
                  to={isAuthenticated ? '/documents/generate' : '/register'}
                  state={{ scanId: data.id, url: data.url, jurisdiction: data.jurisdiction, single: true }}
                  className="text-sm text-brand-200 underline hover:text-white"
                >
                  Or fix just one document for &euro;4.99
                </Link>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
