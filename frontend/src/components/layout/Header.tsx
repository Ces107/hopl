import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Shield, Menu, X } from 'lucide-react';
import { useState } from 'react';

export default function Header() {
  const { isAuthenticated, user, logout } = useAuth();
  const [open, setOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 border-b border-gray-200 bg-white/80 backdrop-blur-lg dark:border-gray-800 dark:bg-gray-950/80">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6">
        <Link to="/" className="flex items-center gap-2 text-xl font-bold text-gray-900 dark:text-white">
          <Shield className="h-7 w-7 text-brand-600" />
          <span>HOPL</span>
        </Link>

        {/* Desktop */}
        <nav className="hidden items-center gap-6 md:flex">
          <Link to="/pricing" className="text-sm text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white">
            Pricing
          </Link>
          {isAuthenticated ? (
            <>
              <Link to="/dashboard" className="text-sm text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white">
                Dashboard
              </Link>
              <span className="text-xs text-gray-400">
                {user?.credits ?? 0} credits
              </span>
              <button onClick={logout} className="rounded-lg bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200 dark:bg-gray-800 dark:text-gray-300">
                Log out
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="text-sm text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white">
                Log in
              </Link>
              <Link to="/register" className="rounded-lg bg-brand-600 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700">
                Get Started
              </Link>
            </>
          )}
        </nav>

        {/* Mobile toggle */}
        <button onClick={() => setOpen(!open)} className="md:hidden">
          {open ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
        </button>
      </div>

      {/* Mobile menu */}
      {open && (
        <div className="border-t border-gray-200 bg-white p-4 md:hidden dark:border-gray-800 dark:bg-gray-950">
          <div className="flex flex-col gap-3">
            <Link to="/pricing" onClick={() => setOpen(false)} className="text-gray-700 dark:text-gray-300">Pricing</Link>
            {isAuthenticated ? (
              <>
                <Link to="/dashboard" onClick={() => setOpen(false)} className="text-gray-700 dark:text-gray-300">Dashboard</Link>
                <button onClick={() => { logout(); setOpen(false); }} className="text-left text-gray-700 dark:text-gray-300">Log out</button>
              </>
            ) : (
              <>
                <Link to="/login" onClick={() => setOpen(false)} className="text-gray-700 dark:text-gray-300">Log in</Link>
                <Link to="/register" onClick={() => setOpen(false)} className="text-brand-600 font-medium">Get Started</Link>
              </>
            )}
          </div>
        </div>
      )}
    </header>
  );
}
