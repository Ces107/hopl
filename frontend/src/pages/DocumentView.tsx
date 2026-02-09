import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../api/client';
import { GeneratedDocument } from '../types';
import ReactMarkdown from 'react-markdown';
import { Download, ArrowLeft, FileText } from 'lucide-react';

export default function DocumentView() {
  const { id } = useParams();
  const [doc, setDoc] = useState<GeneratedDocument | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get(`/documents/${id}`).then(r => setDoc(r.data)).finally(() => setLoading(false));
  }, [id]);

  const downloadPdf = async () => {
    try {
      const response = await api.get(`/documents/${id}/pdf`, { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const a = document.createElement('a');
      a.href = url;
      a.download = (doc?.title || 'document').replace(/[^a-zA-Z0-9.-]/g, '_') + '.pdf';
      a.click();
      window.URL.revokeObjectURL(url);
    } catch {
      alert('Failed to download PDF');
    }
  };

  if (loading) return <div className="flex h-96 items-center justify-center"><div className="h-8 w-8 animate-spin rounded-full border-4 border-brand-600 border-t-transparent" /></div>;
  if (!doc) return <div className="py-20 text-center text-gray-500">Document not found</div>;

  return (
    <div className="mx-auto max-w-4xl px-4 py-10">
      {/* Header */}
      <div className="mb-6 flex items-center justify-between">
        <Link to="/dashboard" className="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700">
          <ArrowLeft className="h-4 w-4" /> Back to Dashboard
        </Link>
        <button onClick={downloadPdf}
          className="flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700">
          <Download className="h-4 w-4" /> Download PDF
        </button>
      </div>

      {/* Document meta */}
      <div className="mb-6 flex items-center gap-3 rounded-xl bg-gray-50 p-4 dark:bg-gray-900">
        <FileText className="h-8 w-8 text-brand-600" />
        <div>
          <h1 className="text-xl font-bold text-gray-900 dark:text-white">{doc.title}</h1>
          <p className="text-sm text-gray-500">
            {doc.documentType.replace(/_/g, ' ')} &middot; {doc.jurisdiction?.replace('_', ' ')} &middot; {new Date(doc.createdAt).toLocaleDateString()}
          </p>
        </div>
      </div>

      {/* Content */}
      <div className="prose prose-gray max-w-none rounded-xl border border-gray-200 bg-white p-8 dark:border-gray-800 dark:bg-gray-950 dark:prose-invert">
        <ReactMarkdown>{doc.content}</ReactMarkdown>
      </div>
    </div>
  );
}
