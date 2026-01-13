'use client';

import { Card } from '@/components/ui/card';
import { useMetrics } from '@/lib/queries';

export default function MetricsPage() {
  const { data } = useMetrics();

  return (
    <div className="grid gap-6 lg:grid-cols-2">
      <Card>
        <p className="text-sm font-semibold text-white">Orders status distribution</p>
        <div className="mt-4 space-y-4">
          {data &&
            Object.entries(data.ordersByStatus).map(([status, count]) => (
              <div key={status}>
                <div className="flex justify-between text-xs text-slate-400">
                  <span>{status}</span>
                  <span>{count}</span>
                </div>
                <div className="mt-2 h-2 rounded-full bg-slate-800">
                  <div
                    className="h-2 rounded-full bg-blue-200"
                    style={{ width: `${Math.min(count * 12, 100)}%` }}
                  />
                </div>
              </div>
            ))}
        </div>
      </Card>
      <Card>
        <p className="text-sm font-semibold text-white">Ticket priority distribution</p>
        <div className="mt-4 space-y-4">
          {data &&
            Object.entries(data.ticketsByPriority).map(([priority, count]) => (
              <div key={priority}>
                <div className="flex justify-between text-xs text-slate-400">
                  <span>{priority}</span>
                  <span>{count}</span>
                </div>
                <div className="mt-2 h-2 rounded-full bg-slate-800">
                  <div
                    className="h-2 rounded-full bg-emerald-300"
                    style={{ width: `${Math.min(count * 12, 100)}%` }}
                  />
                </div>
              </div>
            ))}
        </div>
      </Card>
    </div>
  );
}
