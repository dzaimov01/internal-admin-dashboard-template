'use client';

import { ReactNode } from 'react';
import { useAuth } from '@/lib/auth';
import { Card } from '@/components/ui/card';

export function AdminGate({ children }: { children: ReactNode }) {
  const { hasRole } = useAuth();
  if (!hasRole('Admin')) {
    return (
      <Card>
        <p className="text-sm font-semibold text-white">Restricted</p>
        <p className="text-xs text-slate-400">
          This section requires Admin privileges.
        </p>
      </Card>
    );
  }
  return <>{children}</>;
}
