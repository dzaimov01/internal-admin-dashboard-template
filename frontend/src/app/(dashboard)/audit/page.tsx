'use client';

import { useState } from 'react';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { useAuditLogs } from '@/lib/queries';

export default function AuditPage() {
  const [actorEmail, setActorEmail] = useState('');
  const [action, setAction] = useState('');
  const [entityType, setEntityType] = useState('');
  const { data } = useAuditLogs(actorEmail || undefined, action || undefined, entityType || undefined);

  return (
    <Card>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p className="text-sm font-semibold text-white">Audit trail</p>
          <p className="text-xs text-slate-400">Immutable record of who did what and when.</p>
        </div>
        <div className="flex flex-wrap items-center gap-2">
          <Input
            placeholder="Actor email"
            value={actorEmail}
            onChange={(e) => setActorEmail(e.target.value)}
            className="h-9 w-40"
          />
          <Input
            placeholder="Action"
            value={action}
            onChange={(e) => setAction(e.target.value)}
            className="h-9 w-28"
          />
          <Input
            placeholder="Entity"
            value={entityType}
            onChange={(e) => setEntityType(e.target.value)}
            className="h-9 w-28"
          />
        </div>
      </div>
      <div className="mt-6 overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead className="text-xs uppercase text-slate-500">
            <tr>
              <th className="py-2">Actor</th>
              <th className="py-2">Action</th>
              <th className="py-2">Entity</th>
              <th className="py-2">When</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800">
            {data?.map((log) => (
              <tr key={log.id} className="text-slate-200">
                <td className="py-3 text-slate-300">{log.actorEmail}</td>
                <td className="py-3 font-medium text-white">{log.action}</td>
                <td className="py-3 text-slate-300">
                  {log.entityType} #{log.entityId}
                </td>
                <td className="py-3 text-slate-400">
                  {new Date(log.createdAt).toLocaleString()}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Card>
  );
}
