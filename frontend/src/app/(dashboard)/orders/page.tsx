'use client';

import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { apiClient } from '@/lib/api';
import { useCustomers, useOrders } from '@/lib/queries';
import { z } from 'zod';

export default function OrdersPage() {
  const [statusFilter, setStatusFilter] = useState('');
  const [sortField, setSortField] = useState('createdAt');
  const [sortDirection, setSortDirection] = useState('DESC');
  const { data: orders } = useOrders(statusFilter || undefined, sortField, sortDirection);
  const { data: customers } = useCustomers();
  const [customerId, setCustomerId] = useState('');
  const [amount, setAmount] = useState('');
  const [bulkIds, setBulkIds] = useState('');
  const [bulkStatus, setBulkStatus] = useState('PROCESSING');
  const queryClient = useQueryClient();

  const createMutation = useMutation({
    mutationFn: () =>
      apiClient.post('/api/orders', {
        customerId: Number(customerId),
        amount: Number(amount),
        status: 'NEW'
      }),
    onSuccess: () => {
      setCustomerId('');
      setAmount('');
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    }
  });

  const bulkMutation = useMutation({
    mutationFn: () =>
      apiClient.post('/api/orders/bulk/status', {
        ids: bulkIds.split(',').map((id) => Number(id.trim())).filter(Boolean),
        status: bulkStatus
      }),
    onSuccess: () => {
      setBulkIds('');
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    }
  });

  const handleCreate = () => {
    const schema = z.object({
      customerId: z.string().min(1),
      amount: z.coerce.number().positive()
    });
    const result = schema.safeParse({ customerId, amount });
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
            <p className="text-sm font-semibold text-white">Orders</p>
            <p className="text-xs text-slate-400">Track order status and values.</p>
          </div>
          <div className="flex items-center gap-2">
            <select
              className="h-9 rounded-md border border-slate-700 bg-slate-900/60 px-3 text-xs text-slate-100"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="">All statuses</option>
              {['NEW', 'PROCESSING', 'FULFILLED', 'CANCELLED'].map((status) => (
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
              <option value="createdAt">Created</option>
              <option value="amount">Amount</option>
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
                <th className="py-2">Customer</th>
                <th className="py-2">Amount</th>
                <th className="py-2">Status</th>
                <th className="py-2">Created</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {orders?.content.map((order) => (
                <tr key={order.id} className="text-slate-200">
                  <td className="py-3 font-medium text-white">{order.customer?.name}</td>
                  <td className="py-3 text-slate-300">${order.amount}</td>
                  <td className="py-3 text-slate-300">{order.status}</td>
                  <td className="py-3 text-slate-400">
                    {new Date(order.createdAt).toLocaleDateString()}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      <Card>
        <p className="text-sm font-semibold text-white">Create order</p>
        <p className="text-xs text-slate-400">Associate an order with an existing customer.</p>
        <div className="mt-4 space-y-3">
          <select
            className="h-10 w-full rounded-md border border-slate-700 bg-slate-900/60 px-3 text-sm text-slate-100"
            value={customerId}
            onChange={(e) => setCustomerId(e.target.value)}
          >
            <option value="">Select customer</option>
            {customers?.content.map((customer) => (
              <option key={customer.id} value={customer.id}>
                {customer.name}
              </option>
            ))}
          </select>
          <Input
            placeholder="Amount"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />
          <Button
            onClick={handleCreate}
            disabled={!customerId || !amount || createMutation.isPending}
          >
            {createMutation.isPending ? 'Creating...' : 'Create'}
          </Button>
        </div>

        <div className="mt-6 space-y-3 border-t border-slate-800 pt-4">
          <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Bulk status</p>
          <Input
            placeholder="Order IDs (comma-separated)"
            value={bulkIds}
            onChange={(e) => setBulkIds(e.target.value)}
          />
          <select
            className="h-10 w-full rounded-md border border-slate-700 bg-slate-900/60 px-3 text-sm text-slate-100"
            value={bulkStatus}
            onChange={(e) => setBulkStatus(e.target.value)}
          >
            {['NEW', 'PROCESSING', 'FULFILLED', 'CANCELLED'].map((status) => (
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
