'use client';

import { Card } from '@/components/ui/card';
import { useSystemHealth } from '@/lib/queries';
import { AdminGate } from '@/components/AdminGate';

export default function SystemPage() {
  const { data } = useSystemHealth();

  return (
    <AdminGate>
      <Card>
        <p className="text-sm font-semibold text-white">System health</p>
        <p className="text-xs text-slate-400">Verify service connectivity and build metadata.</p>
        <div className="mt-6 grid gap-4 md:grid-cols-3">
          <div>
            <p className="text-xs uppercase tracking-wide text-slate-400">Database</p>
            <p className="mt-2 text-lg text-white">
              {data?.databaseOk ? 'Connected' : 'Unavailable'}
            </p>
          </div>
          <div>
            <p className="text-xs uppercase tracking-wide text-slate-400">Build</p>
            <p className="mt-2 text-lg text-white">{data?.buildVersion ?? 'dev'}</p>
          </div>
          <div>
            <p className="text-xs uppercase tracking-wide text-slate-400">Checked</p>
            <p className="mt-2 text-lg text-white">
              {data?.timestamp ? new Date(data.timestamp).toLocaleString() : '—'}
            </p>
          </div>
        </div>
      </Card>
    </AdminGate>
  );
}
