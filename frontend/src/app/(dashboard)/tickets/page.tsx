'use client';

import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { apiClient } from '@/lib/api';
import { useTickets, useUsers } from '@/lib/queries';
import { z } from 'zod';

export default function TicketsPage() {
  const [statusFilter, setStatusFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');
  const [sortField, setSortField] = useState('id');
  const [sortDirection, setSortDirection] = useState('DESC');
  const { data: tickets } = useTickets(
    statusFilter || undefined,
    priorityFilter || undefined,
    sortField,
    sortDirection
  );
  const { data: users } = useUsers();
  const [title, setTitle] = useState('');
  const [priority, setPriority] = useState('MEDIUM');
  const [assignedTo, setAssignedTo] = useState('');
  const [bulkIds, setBulkIds] = useState('');
  const [bulkStatus, setBulkStatus] = useState('IN_PROGRESS');
  const queryClient = useQueryClient();

  const createMutation = useMutation({
    mutationFn: () =>
      apiClient.post('/api/tickets', {
        title,
        priority,
        status: 'OPEN',
        assignedTo: assignedTo ? Number(assignedTo) : null
      }),
    onSuccess: () => {
      setTitle('');
      setAssignedTo('');
      queryClient.invalidateQueries({ queryKey: ['tickets'] });
    }
  });

  const bulkMutation = useMutation({
    mutationFn: () =>
      apiClient.post('/api/tickets/bulk/status', {
        ids: bulkIds.split(',').map((id) => Number(id.trim())).filter(Boolean),
        status: bulkStatus
      }),
    onSuccess: () => {
      setBulkIds('');
      queryClient.invalidateQueries({ queryKey: ['tickets'] });
    }
  });

  const handleCreate = () => {
    const schema = z.object({
      title: z.string().min(3)
    });
    const result = schema.safeParse({ title });
    if (!result.success) {
      return;
    }
    createMutation.mutate();
  };

  return (
    <div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
      <Card>
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <p className="text-sm font-semibold text-white">Tickets</p>
            <p className="text-xs text-slate-400">Prioritize operational requests.</p>
          </div>
          <div className="flex items-center gap-2">
            <select
              className="h-9 rounded-md border border-slate-700 bg-slate-900/60 px-3 text-xs text-slate-100"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="">All statuses</option>
              {['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'].map((status) => (
                <option key={status} value={status}>
                  {status}
                </option>
              ))}
            </select>
            <select
              className="h-9 rounded-md border border-slate-700 bg-slate-900/60 px-3 text-xs text-slate-100"
              value={priorityFilter}
              onChange={(e) => setPriorityFilter(e.target.value)}
            >
              <option value="">All priorities</option>
              {['LOW', 'MEDIUM', 'HIGH', 'URGENT'].map((priority) => (
                <option key={priority} value={priority}>
                  {priority}
                </option>
              ))}
            </select>
            <select
              className="h-9 rounded-md border border-slate-700 bg-slate-900/60 px-3 text-xs text-slate-100"
              value={sortField}
              onChange={(e) => setSortField(e.target.value)}
            >
              <option value="id">Newest</option>
              <option value="priority">Priority</option>
              <option value="status">Status</option>
            </select>
            <select
              className="h-9 rounded-md border border-slate-700 bg-slate-900/60 px-3 text-xs text-slate-100"
              value={sortDirection}
              onChange={(e) => setSortDirection(e.target.value)}
            >
              <option value="DESC">Desc</option>
              <option value="ASC">Asc</option>
            </select>
          </div>
        </div>
        <div className="mt-6 overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="text-xs uppercase text-slate-500">
              <tr>
                <th className="py-2">Title</th>
                <th className="py-2">Priority</th>
                <th className="py-2">Status</th>
                <th className="py-2">Assignee</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {tickets?.content.map((ticket) => (
                <tr key={ticket.id} className="text-slate-200">
                  <td className="py-3 font-medium text-white">{ticket.title}</td>
                  <td className="py-3 text-slate-300">{ticket.priority}</td>
                  <td className="py-3 text-slate-300">{ticket.status}</td>
                  <td className="py-3 text-slate-400">
                    {ticket.assignedTo?.fullName ?? 'Unassigned'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      <Card>
        <p className="text-sm font-semibold text-white">Open ticket</p>
        <p className="text-xs text-slate-400">Assign tasks with clear priority.</p>
        <div className="mt-4 space-y-3">
          <Input placeholder="Ticket title" value={title} onChange={(e) => setTitle(e.target.value)} />
          <select
            className="h-10 w-full rounded-md border border-slate-700 bg-slate-900/60 px-3 text-sm text-slate-100"
            value={priority}
            onChange={(e) => setPriority(e.target.value)}
          >
            {['LOW', 'MEDIUM', 'HIGH', 'URGENT'].map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </select>
          <select
            className="h-10 w-full rounded-md border border-slate-700 bg-slate-900/60 px-3 text-sm text-slate-100"
            value={assignedTo}
            onChange={(e) => setAssignedTo(e.target.value)}
          >
            <option value="">Assign to</option>
            {users?.map((user) => (
              <option key={user.id} value={user.id}>
                {user.fullName}
              </option>
            ))}
          </select>
          <Button
            onClick={handleCreate}
            disabled={!title || createMutation.isPending}
          >
            {createMutation.isPending ? 'Creating...' : 'Create'}
          </Button>
        </div>

        <div className="mt-6 space-y-3 border-t border-slate-800 pt-4">
          <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Bulk status</p>
          <Input
            placeholder="Ticket IDs (comma-separated)"
            value={bulkIds}
            onChange={(e) => setBulkIds(e.target.value)}
          />
          <select
            className="h-10 w-full rounded-md border border-slate-700 bg-slate-900/60 px-3 text-sm text-slate-100"
            value={bulkStatus}
            onChange={(e) => setBulkStatus(e.target.value)}
          >
            {['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'].map((status) => (
              <option key={status} value={status}>
                {status}
              </option>
            ))}
          </select>
          <Button
            variant="secondary"
            onClick={() => bulkMutation.mutate()}
            disabled={!bulkIds || bulkMutation.isPending}
          >
            {bulkMutation.isPending ? 'Updating...' : 'Apply bulk status'}
          </Button>
        </div>
      </Card>
    </div>
  );
}
