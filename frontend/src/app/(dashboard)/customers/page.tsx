'use client';

import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { apiClient } from '@/lib/api';
import { useCustomers } from '@/lib/queries';
import { z } from 'zod';

export default function CustomersPage() {
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [sortField, setSortField] = useState('name');
  const [sortDirection, setSortDirection] = useState('ASC');
  const { data } = useCustomers(
    0,
    search || undefined,
    statusFilter || undefined,
    sortField,
    sortDirection
  );
  const queryClient = useQueryClient();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [csvImport, setCsvImport] = useState('');

  const createMutation = useMutation({
    mutationFn: () =>
      apiClient.post('/api/customers', {
        name,
        email,
        status: 'ACTIVE',
        notes: ''
      }),
    onSuccess: () => {
      setName('');
      setEmail('');
      queryClient.invalidateQueries({ queryKey: ['customers'] });
    }
  });

  const importMutation = useMutation({
    mutationFn: () => apiClient.post('/api/customers/import', csvImport),
    onSuccess: () => {
      setCsvImport('');
      queryClient.invalidateQueries({ queryKey: ['customers'] });
    }
  });

  const handleExport = async () => {
    const token = window.localStorage.getItem('accessToken');
    const baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
    const response = await fetch(`${baseUrl}/api/customers/export`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'customers.csv';
    link.click();
    window.URL.revokeObjectURL(url);
  };

  const handleCreate = () => {
    const schema = z.object({
      name: z.string().min(2),
      email: z.string().email()
    });
    const result = schema.safeParse({ name, email });
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
            <p className="text-sm font-semibold text-white">Customers</p>
            <p className="text-xs text-slate-400">Search, filter, and manage customer records.</p>
          </div>
          <div className="flex items-center gap-2">
            <Input
              placeholder="Search customers"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="h-9 w-48"
            />
            <select
              className="h-9 rounded-md border border-slate-700 bg-slate-900/60 px-3 text-xs text-slate-100"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="">All statuses</option>
              {['ACTIVE', 'PAUSED', 'CHURNED'].map((status) => (
                <option key={status} value={status}>
                  {status}
                </option>
              ))}
            </select>
            <select
              className="h-9 rounded-md border border-slate-700 bg-slate-900/60 px-3 text-xs text-slate-100"
              value={sortField}
              onChange={(e) => setSortField(e.target.value)}
            >
              <option value="name">Name</option>
              <option value="email">Email</option>
              <option value="status">Status</option>
            </select>
            <select
              className="h-9 rounded-md border border-slate-700 bg-slate-900/60 px-3 text-xs text-slate-100"
              value={sortDirection}
              onChange={(e) => setSortDirection(e.target.value)}
            >
              <option value="ASC">Asc</option>
              <option value="DESC">Desc</option>
            </select>
            <Button variant="outline" onClick={handleExport}>
              Export CSV
            </Button>
          </div>
        </div>
        <div className="mt-6 overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="text-xs uppercase text-slate-500">
              <tr>
                <th className="py-2">Name</th>
                <th className="py-2">Email</th>
                <th className="py-2">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {data?.content.map((customer) => (
                <tr key={customer.id} className="text-slate-200">
                  <td className="py-3 font-medium text-white">{customer.name}</td>
                  <td className="py-3 text-slate-300">{customer.email}</td>
                  <td className="py-3 text-slate-300">{customer.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      <Card>
        <p className="text-sm font-semibold text-white">Create customer</p>
        <p className="text-xs text-slate-400">Add a new account with default ACTIVE status.</p>
        <div className="mt-4 space-y-3">
          <Input placeholder="Customer name" value={name} onChange={(e) => setName(e.target.value)} />
          <Input placeholder="Email address" value={email} onChange={(e) => setEmail(e.target.value)} />
          <Button
            onClick={handleCreate}
            disabled={!name || !email || createMutation.isPending}
          >
            {createMutation.isPending ? 'Creating...' : 'Create'}
          </Button>
        </div>

        <div className="mt-6 space-y-3 border-t border-slate-800 pt-4">
          <p className="text-xs uppercase tracking-[0.3em] text-slate-400">CSV import</p>
          <Textarea
            placeholder="id,name,email,status,notes"
            value={csvImport}
            onChange={(e) => setCsvImport(e.target.value)}
          />
          <Button
            variant="secondary"
            onClick={() => importMutation.mutate()}
            disabled={!csvImport || importMutation.isPending}
          >
            {importMutation.isPending ? 'Importing...' : 'Import CSV'}
          </Button>
        </div>
      </Card>
    </div>
  );
}
