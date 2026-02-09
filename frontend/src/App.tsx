import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Header from './components/layout/Header';
import Landing from './pages/Landing';
import ScanResult from './pages/ScanResult';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import DocumentGenerator from './pages/DocumentGenerator';
import DocumentView from './pages/DocumentView';
import Pricing from './pages/Pricing';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="min-h-screen bg-white dark:bg-gray-950">
          <Header />
          <Routes>
            <Route path="/" element={<Landing />} />
            <Route path="/scan/:id" element={<ScanResult />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/documents/generate" element={<DocumentGenerator />} />
            <Route path="/documents/:id" element={<DocumentView />} />
            <Route path="/pricing" element={<Pricing />} />
          </Routes>
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}
