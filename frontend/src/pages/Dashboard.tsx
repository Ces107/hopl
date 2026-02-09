import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import { GeneratedDocument } from '../types';
import { FileText, Plus, Search, Download } from 'lucide-react';

export default function Dashboard() {
  const { user, isAuthenticated } = useAuth();
  const [documents, setDocuments] = useState<GeneratedDocument[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (isAuthenticated) {
      api.get('/documents').then(r => setDocuments(r.data)).finally(() => setLoading(false));
    }
  }, [isAuthenticated]);

  if (!isAuthenticated) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <div className="text-center">
          <h2 className="mb-4 text-2xl font-bold text-gray-900 dark:text-white">Please log in</h2>
          <Link to="/login" className="rounded-lg bg-brand-600 px-6 py-3 font-medium text-white">Log in</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-10">
      {/* Welcome */}
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
            Welcome{user?.name ? `, ${user.name}` : ''}
          </h1>
          <p className="text-gray-500">
            Plan: <span className="font-medium text-brand-600">{user?.plan || 'FREE'}</span>
            {' '}&middot; Credits: <span className="font-medium">{user?.credits ?? 0}</span>
          </p>
        </div>
        <div className="flex gap-3">
          <Link to="/" className="flex items-center gap-2 rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-300">
            <Search className="h-4 w-4" /> New Scan
          </Link>
          <Link to="/documents/generate" className="flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700">
            <Plus className="h-4 w-4" /> New Document
          </Link>
        </div>
      </div>

      {/* Documents */}
      <h2 className="mb-4 text-lg font-semibold text-gray-900 dark:text-white">Your Documents</h2>
      {loading ? (
        <div className="flex h-40 items-center justify-center">
          <div className="h-6 w-6 animate-spin rounded-full border-2 border-brand-600 border-t-transparent" />
        </div>
      ) : documents.length === 0 ? (
        <div className="rounded-2xl border-2 border-dashed border-gray-200 p-12 text-center dark:border-gray-800">
          <FileText className="mx-auto mb-4 h-12 w-12 text-gray-300" />
          <h3 className="mb-2 text-lg font-medium text-gray-900 dark:text-white">No documents yet</h3>
          <p className="mb-6 text-sm text-gray-500">Scan your website first, then generate the documents you need.</p>
          <Link to="/" className="rounded-lg bg-brand-600 px-6 py-2.5 text-sm font-medium text-white hover:bg-brand-700">
            Scan Your Website
          </Link>
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {documents.map((doc) => (
            <Link key={doc.id} to={`/documents/${doc.id}`}
              className="group rounded-xl border border-gray-200 p-5 transition hover:border-brand-300 hover:shadow-md dark:border-gray-800">
              <div className="mb-3 flex items-center gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-brand-50 dark:bg-brand-900/20">
                  <FileText className="h-5 w-5 text-brand-600" />
                </div>
                <div className="min-w-0 flex-1">
                  <h3 className="truncate text-sm font-medium text-gray-900 group-hover:text-brand-600 dark:text-white">
                    {doc.title}
                  </h3>
                  <p className="text-xs text-gray-500">{doc.documentType.replace(/_/g, ' ')}</p>
                </div>
              </div>
              <div className="flex items-center justify-between text-xs text-gray-400">
                <span>{new Date(doc.createdAt).toLocaleDateString()}</span>
                <Download className="h-3.5 w-3.5" />
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
