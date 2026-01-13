'use client';

import { Button } from '@/components/ui/button';
import { ThemeToggle } from '@/components/ThemeToggle';
import { useAuth } from '@/lib/auth';

export function Topbar() {
  const { user, logout } = useAuth();

  return (
    <header className="flex items-center justify-between border-b border-slate-800 bg-slate-950/60 px-6 py-4">
      <div>
        <p className="text-xs uppercase tracking-[0.2em] text-slate-400">Workspace</p>
        <h2 className="text-lg font-semibold text-slate-100">Acme Operations</h2>
      </div>
      <div className="flex items-center gap-4">
        <ThemeToggle />
        <div className="text-right">
          <p className="text-sm font-medium text-slate-100">{user?.fullName || 'Loading...'}</p>
          <p className="text-xs text-slate-400">{user?.email}</p>
        </div>
        <Button variant="outline" onClick={logout}>Sign out</Button>
      </div>
    </header>
  );
}
