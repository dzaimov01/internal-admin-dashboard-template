'use client';

import { Card } from '@/components/ui/card';
import { useMetrics } from '@/lib/queries';

export default function DashboardPage() {
  const { data } = useMetrics();

  return (
    <div className="space-y-6">
      <section className="grid gap-4 lg:grid-cols-3">
        <Card>
          <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Active Customers</p>
          <p className="mt-3 text-3xl font-semibold text-white">
            {data?.activeCustomers ?? '—'}
          </p>
        </Card>
        <Card>
          <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Orders Fulfilled</p>
          <p className="mt-3 text-3xl font-semibold text-white">
            {data?.ordersByStatus?.FULFILLED ?? '—'}
          </p>
        </Card>
        <Card>
          <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Urgent Tickets</p>
          <p className="mt-3 text-3xl font-semibold text-white">
            {data?.ticketsByPriority?.URGENT ?? '—'}
          </p>
        </Card>
      </section>

      <section className="grid gap-4 lg:grid-cols-2">
        <Card>
          <p className="text-sm font-semibold text-white">Orders by status</p>
          <div className="mt-4 space-y-3">
            {data &&
              Object.entries(data.ordersByStatus).map(([status, count]) => (
                <div key={status} className="flex items-center gap-3">
                  <span className="w-28 text-xs text-slate-400">{status}</span>
                  <div className="h-2 flex-1 rounded-full bg-slate-800">
                    <div
                      className="h-2 rounded-full bg-slate-200"
                      style={{ width: `${Math.min(count * 10, 100)}%` }}
                    />
                  </div>
                  <span className="w-10 text-right text-xs text-slate-300">{count}</span>
                </div>
              ))}
          </div>
        </Card>
        <Card>
          <p className="text-sm font-semibold text-white">Tickets by priority</p>
          <div className="mt-4 space-y-3">
            {data &&
              Object.entries(data.ticketsByPriority).map(([priority, count]) => (
                <div key={priority} className="flex items-center gap-3">
                  <span className="w-28 text-xs text-slate-400">{priority}</span>
                  <div className="h-2 flex-1 rounded-full bg-slate-800">
                    <div
                      className="h-2 rounded-full bg-emerald-300"
                      style={{ width: `${Math.min(count * 15, 100)}%` }}
                    />
                  </div>
                  <span className="w-10 text-right text-xs text-slate-300">{count}</span>
                </div>
              ))}
          </div>
        </Card>
      </section>
    </div>
  );
}
